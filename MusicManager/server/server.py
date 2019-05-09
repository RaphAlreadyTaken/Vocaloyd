# coding=utf-8

import sys, Ice
import discotheque
import json
import os
import pymongo
import time
import vlc

from pymongo.collation import Collation

#TODO : rajouter genres dans construction morceaux (recherches)
class clientManagementI(discotheque.clientManagement):
    nbClients = 0
    nbMaxClients = 101
    basePort = 10100
    clients = {}

    def __init__(self, *args, **kwargs):
        #Clients: (port libre: booléen, player associé: media_list_player)
        for i in range(self.basePort, self.basePort + self.nbMaxClients):
            self.clients[i] = [True, None]
    
    def subscribe(self, current=None):
        if self.nbClients == self.nbMaxClients:
            print("No more ports available")
            return -1
        else:
            i = 0
            for i in range(self.basePort, self.basePort + self.nbMaxClients):
                if self.clients[i][0] is True:
                    self.clients[i][0] = False
                    self.clients[i][1] = None
                    self.nbClients += 1
                    print("Port " + str(i) + " given to client")
                    break
                else:
                    continue
            return i

    def unsubscribe(self, port, current=None):
        trackManagementI.stop(port)
        self.clients[port] = [True, None]
        self.nbClients -= 1
        print("Client with port " + str(port) + " unsubscribed")

class trackManagementI(discotheque.trackManagement):

    client = pymongo.MongoClient("mongodb+srv://raph:Multani55%2b@cluster0-guvid.gcp.mongodb.net/test?retryWrites=true")
    db = client.db
    collecMusique = db['musiquePath']
    localPath = os.path.join(os.path.dirname(__file__), 'tracks/')
    vlcInst = vlc.Instance()

    def ajouterTitre(self, song, current=None):
        query = self.collecMusique.insert_one(song.__dict__)
        queryCount = self.collecMusique.count_documents({})
        log = queryResult(sys._getframe().f_code.co_name, queryCount)
        print(log)
        return "Titre ajouté dans la base"

    def recupererTitres(self, current=None):
        musiques = []
        query = self.collecMusique.find({}, {"_id": 0})
        queryCount = self.collecMusique.count_documents({})
        for musique in query:
            track = discotheque.Morceau(musique['artiste'], musique['album'], musique['titre'], musique['file'])
            musiques.append(track)
        log = queryResult(sys._getframe().f_code.co_name, queryCount)
        print(log)
        return musiques

    #Recherche générique (toutes informations confondues)
    def rechercher(self, info, current=None):
        musiques = []
        query = self.collecMusique.find({"$or": [{"titre": info}, {"artiste": info}, {"album": info}, {"genre": info}]}, {"_id": 0}).collation(Collation(locale = 'fr', strength = 2))
        queryCount = self.collecMusique.count_documents({"$or": [{"titre": info}, {"artiste": info}, {"album": info}, {"genre": info}]}, collation = Collation(locale = 'fr', strength = 2))
        for musique in query:
            track = discotheque.Morceau(musique['artiste'], musique['album'], musique['titre'], musique['file'])
            musiques.append(track)
        log = queryResult(sys._getframe().f_code.co_name, queryCount)
        print(log)
        return musiques

    def rechercherParTitre(self, title, current=None):
        musiques = []
        query = self.collecMusique.find({"titre": title}, {"_id": 0}).collation(Collation(locale = 'fr', strength = 2))
        queryCount = self.collecMusique.count_documents({"titre": title}, collation = Collation(locale = 'fr', strength = 2))
        for musique in query:
            track = discotheque.Morceau(musique['artiste'], musique['album'], musique['titre'], musique['file'])
            musiques.append(track)
        log = queryResult(sys._getframe().f_code.co_name, queryCount)
        print(log)
        print(query)
        return musiques

    def rechercherParArtiste(self, artist, current=None):
        musiques = []
        query = self.collecMusique.find({"artiste": artist}, {"_id": 0}).collation(Collation(locale = 'fr', strength = 2))
        queryCount = query.count_documents({"artiste": artist}, collation = Collation(locale = 'fr', strength = 2))
        for musique in query:
            track = discotheque.Morceau(musique['artiste'], musique['album'], musique['titre'], musique['file'])
            musiques.append(track)
        log = queryResult(sys._getframe().f_code.co_name, queryCount)
        print(log)
        return musiques

    def rechercherParAlbum(self, album, current=None):
        musiques = []
        query = self.collecMusique.find({"album": album}, {"_id": 0}).collation(Collation(locale = 'fr', strength = 2))
        queryCount = query.count_documents({"album": album}, collation = Collation(locale = 'fr', strength = 2))
        for musique in query:
            track = discotheque.Morceau(musique['artiste'], musique['album'], musique['titre'], musique['file'])
            musiques.append(track)
        log = queryResult(sys._getframe().f_code.co_name, queryCount)
        print(log)
        return musiques

    def rechercherParGenre(self, genre, current=None):
        musiques = []
        query = self.collecMusique.find({"genre": genre}, {"_id": 0}).collation(Collation(locale = 'fr', strength = 2))
        queryCount = query.count_documents({"genre": genre}, collation = Collation(locale = 'fr', strength = 2))
        for musique in query:
            track = discotheque.Morceau(musique['artiste'], musique['album'], musique['titre'], musique['file'])
            musiques.append(track)
        log = queryResult(sys._getframe().f_code.co_name, queryCount)
        print(log)
        return musiques
 
    def supprimerTitre(self, title, artist, current=None):
        query = self.collecMusique.delete_one({"titre": title, "artiste": artist}, collation = Collation(locale = 'fr', strength = 2))
        queryCount = query.deleted_count
        log = queryResult(sys._getframe().f_code.co_name, queryCount)
        print(log)
        if queryCount == 0:
            return False
        else:
            return True
            
    def supprimerAlbum(self, artist, album, current=None):
        query = self.collecMusique.delete_many({"artiste": artist, "album": album}, collation = Collation(locale = 'fr', strength = 2))
        queryCount = query.deleted_count
        log = queryResult(sys._getframe().f_code.co_name, queryCount)
        print(log)
        if queryCount == 0:
            return False
        else:
            return True

    def supprimerArtiste(self, artist, current=None):
        query = self.collecMusique.delete_many({"artiste": artist}, collation = Collation(locale = 'fr', strength = 2))
        queryCount = query.deleted_count
        log = queryResult(sys._getframe().f_code.co_name, queryCount)
        print(log)
        if queryCount == 0:
            return False
        else:
            return True

    def jouerMorceaux(self, tracks, port, current=None):
        if clientManagementI.clients[port][1] is None:
            clientManagementI.clients[port][1] = self.vlcInst.media_list_player_new()

        player = clientManagementI.clients[port][1]

        if player.is_playing():
            print("Stopping")
            player.release()
            player = self.vlcInst.media_list_player_new()

        target = "/stream"
        localTracks = []

        for track in tracks:
            localTracks.append(self.localPath + str(track.file))

        media = self.vlcInst.media_list_new()

        for track in localTracks:
            media.add_media(self.vlcInst.media_new(track, 'sout=#transcode{vcodec=none,acodec=mp3,ab=128,channels=2,samplerate=44100,scodec=none}:http{mux=mp3,dst=:' + str(port) + target + '}', 'sout-all', 'sout-keep'))
            # media.add_media(self.vlcInst.media_new(track, 'sout=#udp{dst=192.168.1.15:11000/stream}', 'sout-all', 'sout-keep'))

        player.set_media_list(media)
        player.play()

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

    def stop(port, current=None):
        player = clientManagementI.clients[port][1]
        player.release()

def queryResult(funcName, queryCount):
    log = funcName + " -> " + str(queryCount)
    if (queryCount > 1):
        log += " documents concernés"
    else:
        log += " document concerné"
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
    communicator.waitForShutdown()