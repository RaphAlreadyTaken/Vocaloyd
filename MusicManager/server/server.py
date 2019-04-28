# coding=utf-8

import sys, Ice
import discotheque
import json
import os
import pymongo
import vlc
 
client = pymongo.MongoClient("mongodb+srv://raph:Multani55%2b@cluster0-guvid.gcp.mongodb.net/test?retryWrites=true")
db = client.db
collecMusique = db['musiquePath']

class trackManagementI(discotheque.trackManagement):

    localPath = os.path.join(os.path.dirname(__file__), 'tracks/')

    def ajout(self, song, current=None):
        query = collecMusique.insert_one(song.__dict__)
        queryCount = 1
        log = queryResult(sys._getframe().f_code.co_name, queryCount)
        print(log)
        return "Titre ajouté dans la base"

    def recupTitres(self, current=None):
        musiques = []
        query = collecMusique.find({}, {"_id": 0})
        queryCount = query.count()
        for musique in query:
            track = discotheque.Morceau(musique['artiste'], musique['album'], musique['titre'], musique['file'])
            musiques.append(track)
        log = queryResult(sys._getframe().f_code.co_name, queryCount)
        print(log)
        return musiques

    def rechercheParTitre(self, title, current=None):
        musiques = []
        query = collecMusique.find({"titre": title}, {"_id": 0})
        queryCount = query.count()
        for musique in query:
            track = discotheque.Morceau(musique['artiste'], musique['album'], musique['titre'], musique['file'])
            musiques.append(track)
        log = queryResult(sys._getframe().f_code.co_name, queryCount)
        print(log)
        return musiques

    def rechercheParArtiste(self, artist, current=None):
        musiques = []
        query = collecMusique.find({"artiste": artist}, {"_id": 0})
        queryCount = query.count()
        for musique in query:
            track = discotheque.Morceau(musique['artiste'], musique['album'], musique['titre'], musique['file'])
            musiques.append(track)
        log = queryResult(sys._getframe().f_code.co_name, queryCount)
        print(log)
        return musiques
 
    def suppressionTitre(self, title, artist, current=None):
        query = collecMusique.delete_one({"titre": title, "artiste": artist})
        queryCount = query.deleted_count
        log = queryResult(sys._getframe().f_code.co_name, queryCount)
        print(log)
        if queryCount == 0:
            return False
        else:
            return True
            
    def suppressionAlbum(self, artist, album, current=None):
        query = collecMusique.delete_many({"artiste": artist, "album": album})
        queryCount = query.deleted_count
        log = queryResult(sys._getframe().f_code.co_name, queryCount)
        print(log)
        if queryCount == 0:
            return False
        else:
            return True

    # TODO: passer l'adresse du stream en paramètre (retour de la méthode vers le client)
    def jouerTitres(self, title, current=None):
        musiques = self.rechercheParTitre(title)
        print(musiques)

        localTracks = []

        for musique in musiques:
            localTracks.append(self.localPath + str(musique.file))

        vlcInst = vlc.Instance()
        media = vlcInst.media_list_new()

        for track in localTracks:
            media.add_media(vlcInst.media_new(track, 'sout=#transcode{vcodec=none,acodec=mp3,ab=128,channels=2,samplerate=44100,scodec=none}:http{mux=mp3,dst=:10000/stream}', 'sout-all', 'sout_keep'))
            # media.add_media(vlcInst.media_new(track, 'sout=#udp{dst=192.168.1.15:11000/stream}', 'sout-all', 'sout_keep'))

        player = vlcInst.media_list_player_new()
        player.set_media_list(media)
        player.play()


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