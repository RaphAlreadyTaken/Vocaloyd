# coding=utf-8

import sys, Ice
import discotheque
import json
import os
import pymongo
import vlc

from pymongo.collation import Collation

client = pymongo.MongoClient("mongodb+srv://raph:Multani55%2b@cluster0-guvid.gcp.mongodb.net/test?retryWrites=true")
db = client.db
collecMusique = db['musiquePath']

class trackManagementI(discotheque.trackManagement):

    localPath = os.path.join(os.path.dirname(__file__), 'tracks/')

    #TODO: gérer avec une liste/map de ports (cf. jouerMorceaux)
    port = "10000"

    def ajouterTitre(self, song, current=None):
        query = collecMusique.insert_one(song.__dict__)
        queryCount = collecMusique.count_documents({})
        log = queryResult(sys._getframe().f_code.co_name, queryCount)
        print(log)
        return "Titre ajouté dans la base"

    def recupererTitres(self, current=None):
        musiques = []
        query = collecMusique.find({}, {"_id": 0})
        queryCount = collecMusique.count_documents({})
        for musique in query:
            track = discotheque.Morceau(musique['artiste'], musique['album'], musique['titre'], musique['file'])
            musiques.append(track)
        log = queryResult(sys._getframe().f_code.co_name, queryCount)
        print(log)
        return musiques

    def rechercherParTitre(self, title, current=None):
        musiques = []
        query = collecMusique.find({"titre": title}, {"_id": 0}).collation(Collation(locale = 'fr', strength = 2))
        queryCount = collecMusique.count_documents({"titre": title}, collation = Collation(locale = 'fr', strength = 2))
        for musique in query:
            track = discotheque.Morceau(musique['artiste'], musique['album'], musique['titre'], musique['file'])
            musiques.append(track)
        log = queryResult(sys._getframe().f_code.co_name, queryCount)
        print(log)
        return musiques

    def rechercherParArtiste(self, artist, current=None):
        musiques = []
        query = collecMusique.find({"artiste": artist}, {"_id": 0}).collation(Collation(locale = 'fr', strength = 2))
        queryCount = query.count_documents({"artiste": artist}, collation = Collation(locale = 'fr', strength = 2))
        for musique in query:
            track = discotheque.Morceau(musique['artiste'], musique['album'], musique['titre'], musique['file'])
            musiques.append(track)
        log = queryResult(sys._getframe().f_code.co_name, queryCount)
        print(log)
        return musiques
 
    def supprimerTitre(self, title, artist, current=None):
        query = collecMusique.delete_one({"titre": title, "artiste": artist}, collation = Collation(locale = 'fr', strength = 2))
        queryCount = query.deleted_count
        log = queryResult(sys._getframe().f_code.co_name, queryCount)
        print(log)
        if queryCount == 0:
            return False
        else:
            return True
            
    def supprimerAlbum(self, artist, album, current=None):
        query = collecMusique.delete_many({"artiste": artist, "album": album}, collation = Collation(locale = 'fr', strength = 2))
        queryCount = query.deleted_count
        log = queryResult(sys._getframe().f_code.co_name, queryCount)
        print(log)
        if queryCount == 0:
            return False
        else:
            return True

    def supprimerArtiste(self, artist, current=None):
        query = collecMusique.delete_many({"artiste": artist}, collation = Collation(locale = 'fr', strength = 2))
        queryCount = query.deleted_count
        log = queryResult(sys._getframe().f_code.co_name, queryCount)
        print(log)
        if queryCount == 0:
            return False
        else:
            return True

    #TODO : gérer les ports (multiples clients). Idée: map de ports <num, dispo>, dispos en haut. Quand nouveau client, attribution, modif dispo, tri.
    def jouerMorceaux(self, tracks, current=None):
        target = self.port + "/stream"

        localTracks = []

        for track in tracks:
            localTracks.append(self.localPath + str(track.file))

        vlcInst = vlc.Instance()
        media = vlcInst.media_list_new()

        for track in localTracks:
            media.add_media(vlcInst.media_new(track, 'sout=#transcode{vcodec=none,acodec=mp3,ab=128,channels=2,samplerate=44100,scodec=none}:http{mux=mp3,dst=:' + target + '}', 'sout-all', 'sout_keep'))
            # media.add_media(vlcInst.media_new(track, 'sout=#udp{dst=192.168.1.15:11000/stream}', 'sout-all', 'sout_keep'))

        player = vlcInst.media_list_player_new()
        player.set_media_list(media)
        player.play()

        return target

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
props.setProperty("Ice.MessageSizeMax", "40000")

initData = Ice.InitializationData()
initData.properties = props
print(initData.properties)

with Ice.initialize(initData) as communicator:
    adapter = communicator.createObjectAdapterWithEndpoints("SimpleManagerAdapter", "default -p 10000")
    object = trackManagementI()
    adapter.add(object, communicator.stringToIdentity("SimpleManager"))
    adapter.activate()
    communicator.waitForShutdown()