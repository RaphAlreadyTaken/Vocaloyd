# coding=utf-8

import base64
import Ice
import json
import os
import pymongo
import sys
import threading
import time
import traceback
import vlc

from discotheque import *
from pymongo.collation import Collation

class clientManagementI(clientManagement):
    nbClients = 0
    nbMaxClients = 101
    basePort = 10100
    clients = {}

    def __init__(self, *args, **kwargs):
        #Clients: (ip port associated: boolean, associated player: media_list_player, associated player: media_player (for track info), subscription time: seconds since epoch)
        for i in range(self.basePort, self.basePort + self.nbMaxClients):
            self.clients[i] = [True, None, None, None]
    
    def subscribe(self, current=None):
        if self.nbClients == self.nbMaxClients:
            print("No more ports available")
            return -1
        else:
            i = 0
            for i in range(self.basePort, self.basePort + self.nbMaxClients):
                if self.clients[i][0] == True:
                    self.clients[i][0] = False
                    self.clients[i][1] = None
                    self.clients[i][2] = None
                    self.clients[i][3] = Chrono.getCurrentTime(Chrono)
                    self.nbClients += 1
                    print("Port " + str(i) + " given to client")
                    break
                else:
                    continue
            return i

    def unsubscribe(self, port, current=None):
        trackManagementI.stop(trackManagementI, port)
        self.clients[port] = [True, None, None, None]
        self.nbClients -= 1
        print("Client with port " + str(port) + " unsubscribed")

class trackManagementI(trackManagement):
    client = pymongo.MongoClient("mongodb+srv://raph:Multani55%2b@cluster0-guvid.gcp.mongodb.net/test?retryWrites=true")
    db = client.db
    collecMusique = db['musiqueRaph']
    localPath = os.path.join(os.path.dirname(__file__), 'tracks/')
    vlcInst = vlc.Instance()

    def ajouterTitre(self, song, current=None):
        query = self.collecMusique.insert_one(song.__dict__)
        queryCount = 1
        log = queryResult(sys._getframe().f_code.co_name, queryCount)
        print(log)
        return "Track added to base"

    def recupererTitres(self, current=None):
        musiques = []
        query = self.collecMusique.find({}, {"_id": 0})
        queryCount = self.collecMusique.count_documents({})
        for musique in query:
            track = Morceau(musique['titre'], musique['artiste'], musique['album'], musique['genre'], musique['piste'], musique['image'], musique['fichier'])
            musiques.append(track)
        log = queryResult(sys._getframe().f_code.co_name, queryCount)
        print(log)
        return musiques

    #Generic search (all criteria)
    def rechercher(self, info, current=None):
        musiques = []
        query = self.collecMusique.find({"$or": [{"titre": info}, {"artiste": info}, {"album": info}, {"genre": info}]}, {"_id": 0}).collation(Collation(locale = 'fr', strength = 1))
        queryCount = self.collecMusique.count_documents({"$or": [{"titre": info}, {"artiste": info}, {"album": info}, {"genre": info}]}, collation = Collation(locale = 'fr', strength = 1))
        for musique in query:
            track = Morceau(musique['titre'], musique['artiste'], musique['album'], musique['genre'], musique['piste'], musique['image'], musique['fichier'])
            musiques.append(track)
        log = queryResult(sys._getframe().f_code.co_name, queryCount)
        print(log)
        return musiques

    def rechercherParTitre(self, title, current=None):
        musiques = []
        query = self.collecMusique.find({"titre": title}, {"_id": 0}).collation(Collation(locale = 'fr', strength = 1))
        queryCount = self.collecMusique.count_documents({"titre": title}, collation = Collation(locale = 'fr', strength = 1))
        for musique in query:
            track = Morceau(musique['titre'], musique['artiste'], musique['album'], musique['genre'], musique['piste'], musique['image'], musique['fichier'])
            musiques.append(track)
        log = queryResult(sys._getframe().f_code.co_name, queryCount)
        print(log)
        return musiques

    def rechercherParArtiste(self, artist, current=None):
        musiques = []
        query = self.collecMusique.find({"artiste": artist}, {"_id": 0}).collation(Collation(locale = 'fr', strength = 1))
        queryCount = self.collecMusique.count_documents({"artiste": artist}, collation = Collation(locale = 'fr', strength = 1))
        for musique in query:
            track = Morceau(musique['titre'], musique['artiste'], musique['album'], musique['genre'], musique['piste'], musique['image'], musique['fichier'])
            musiques.append(track)
        log = queryResult(sys._getframe().f_code.co_name, queryCount)
        print(log)
        return musiques

    def rechercherParAlbum(self, album, current=None):
        musiques = []
        query = self.collecMusique.find({"album": album}, {"_id": 0}).collation(Collation(locale = 'fr', strength = 1)).sort("piste", pymongo.ASCENDING)
        queryCount = self.collecMusique.count_documents({"album": album}, collation = Collation(locale = 'fr', strength = 1))
        for musique in query:
            track = Morceau(musique['titre'], musique['artiste'], musique['album'], musique['genre'], musique['piste'], musique['image'], musique['fichier'])
            musiques.append(track)
        log = queryResult(sys._getframe().f_code.co_name, queryCount)
        print(log)
        return musiques

    def rechercherParGenre(self, genre, current=None):
        musiques = []
        query = self.collecMusique.find({"genre": genre}, {"_id": 0}).collation(Collation(locale = 'fr', strength = 1))
        queryCount = self.collecMusique.count_documents({"genre": genre}, collation = Collation(locale = 'fr', strength = 1))
        for musique in query:
            track = Morceau(musique['titre'], musique['artiste'], musique['album'], musique['genre'], musique['piste'], musique['image'], musique['fichier'])
            musiques.append(track)
        log = queryResult(sys._getframe().f_code.co_name, queryCount)
        print(log)
        return musiques
 
    def supprimerTitre(self, title, artist, current=None):
        query = self.collecMusique.delete_one({"titre": title, "artiste": artist}, collation = Collation(locale = 'fr', strength = 1))
        queryCount = query.deleted_count
        log = queryResult(sys._getframe().f_code.co_name, queryCount)
        print(log)
        if queryCount == 0:
            return False
        else:
            return True
            
    def supprimerAlbum(self, artist, album, current=None):
        query = self.collecMusique.delete_many({"artiste": artist, "album": album}, collation = Collation(locale = 'fr', strength = 1))
        queryCount = query.deleted_count
        log = queryResult(sys._getframe().f_code.co_name, queryCount)
        print(log)
        if queryCount == 0:
            return False
        else:
            return True

    def supprimerArtiste(self, artist, current=None):
        query = self.collecMusique.delete_many({"artiste": artist}, collation = Collation(locale = 'fr', strength = 1))
        queryCount = query.deleted_count
        log = queryResult(sys._getframe().f_code.co_name, queryCount)
        print(log)
        if queryCount == 0:
            return False
        else:
            return True

    def jouerMorceaux(self, tracks, port, current=None):
        player = clientManagementI.clients[port][1]
        innerPlayer = clientManagementI.clients[port][2]

        if player is not None:
            player.release()
            print("Releasing player")
        if innerPlayer is not None:
            innerPlayer.release()
            print("Releasing inner player")

        player = self.vlcInst.media_list_player_new()
        innerPlayer = self.vlcInst.media_player_new()
        player.set_media_player(innerPlayer)

        target = "/stream"
        localTracks = []

        for track in tracks:
            localTracks.append(self.localPath + str(track.fichier))

        media = self.vlcInst.media_list_new()

        for track in localTracks:
            media.add_media(self.vlcInst.media_new(track, 'sout=#transcode{vcodec=none,acodec=mp3,ab=128,channels=2,samplerate=44100,scodec=none}:http{mux=mp3,dst=:' + str(port) + target + '}', 'sout-all', 'sout-keep'))
            # media.add_media(self.vlcInst.media_new(track, 'sout=#udp{dst=192.168.43.15:11000/stream}', 'sout-all', 'sout-keep'))

        player.set_media_list(media)
        player.play()

        clientManagementI.clients[port][1] = player
        clientManagementI.clients[port][2] = innerPlayer

        return target

    def playPause(self, port, current=None):
        if clientManagementI.clients[port][1] is None:
            return
        else:
            player = clientManagementI.clients[port][1]

            if player.is_playing():
                player.pause()
            elif not player.is_playing():
                player.play()
        return
    
    def nextTrack(self, port, current=None):
        if clientManagementI.clients[port][1] is None:
            return
        else:
            player = clientManagementI.clients[port][1]
            player.next()
        return

    def previousTrack(self, port, current=None):
        if clientManagementI.clients[port][1] is None:
            return
        else:
            player = clientManagementI.clients[port][1]
            player.previous()
        return

    def stop(self, port, current=None):
        player = clientManagementI.clients[port][1]

        if player is not None:
            player.release()
        if innerPlayer is not None:
            innerPlayer.release()
        print("Releasing player")

    def getInfos(self, port, current=None):
        clientManagementI.clients[port][3] = Chrono.getCurrentTime(Chrono)  #Resetting client time if activity

        if clientManagementI.clients[port][1] is None or clientManagementI.clients[port][2] is None:
            return
        else:
            player = clientManagementI.clients[port][1]
            innerPlayer = clientManagementI.clients[port][2]
            mediaExtract = innerPlayer.get_media()

            if mediaExtract is not None:
                mediaExtract.parse_with_options(vlc.MediaParseFlag.fetch_local, 0)
            
                result = []

                for i in range(12):
                    if mediaExtract.get_meta(i) is not None:
                        if vlc.Meta._enum_names_[i] == "Description":
                            image = open(self.localPath + str(mediaExtract.get_meta(i)), 'rb').read()
                            imageStr = str(base64.b64encode(image))
                            result.append(Entry("Description", imageStr))
                            continue
                        result.append(Entry(vlc.Meta._enum_names_[i], mediaExtract.get_meta(i)))
                
                return result

class Chrono:
    stop = False    #Stops thread if switched to True
    maxTime = 600   #Max idle time on server (seconds)
    scanFreq = 300  #Frequency of client inactivity check (seconds)

    @staticmethod
    def getCurrentTime(self):
        curTime = time.time()
        return curTime

    @staticmethod
    def getElapsedTime(self, testTime):
        curTime = self.getCurrentTime(self)
        elapsedTime = curTime - testTime
        return elapsedTime
    
    def checkTimeLimit(self):
        while self.stop is False:
            for i in range(clientManagementI.basePort, clientManagementI.basePort + clientManagementI.nbMaxClients):
                if clientManagementI.clients[i][0] is False:
                    actTime = self.getElapsedTime(Chrono, clientManagementI.clients[i][3])

                    if actTime > self.maxTime:
                        print("Client inactive for " + str(actTime) + "s. Goodbye " + str(i))
                        clientManagementI.unsubscribe(clientManagementI, i)

            time.sleep(self.scanFreq)

def queryResult(funcName, queryCount):
    log = funcName + " -> " + str(queryCount)
    if (queryCount > 1):
        log += " documents affected"
    else:
        log += " document affected"
    return log            

props = Ice.createProperties(sys.argv)
props.setProperty("Ice.LogFile", "log")
props.setProperty("Ice.Warn.Connections", "1")
props.setProperty("Ice.Trace.Network", "2")
props.setProperty("Ice.MessageSizeMax", "40000")

initData = Ice.InitializationData()
initData.properties = props
print(initData.properties)

with Ice.initialize(initData) as communicator:
    adapter = communicator.createObjectAdapterWithEndpoints("SimpleManagerAdapter", "default -p 10000")
    object = trackManagementI()
    object2 = clientManagementI()
    adapter.add(object, communicator.stringToIdentity("SimpleManager"))
    adapter.add(object2, communicator.stringToIdentity("SimpleClientManager"))
    adapter.activate()

    threadClientMgmt = threading.Thread(target = Chrono.checkTimeLimit, args = (Chrono,))
    threadClientMgmt.start()

    print("Server running")
    communicator.waitForShutdown()