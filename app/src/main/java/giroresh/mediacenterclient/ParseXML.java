package giroresh.mediacenterclient;

import android.os.AsyncTask;
import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import giroresh.mediacenterclient.playlistItems.filetypes.AudioFiles;
import giroresh.mediacenterclient.playlistItems.filetypes.GBRomsFiles;
import giroresh.mediacenterclient.playlistItems.filetypes.PlaylistItems;
import giroresh.mediacenterclient.playlistItems.filetypes.RomFiles;
import giroresh.mediacenterclient.playlistItems.filetypes.VideoFiles;
import giroresh.mediacenterclient.playlistItems.tags.AudioTags;
import giroresh.mediacenterclient.playlistItems.tags.RomTags;
import giroresh.mediacenterclient.playlistItems.tags.Tags;
import giroresh.mediacenterclient.playlistItems.tags.VideoTags;

/**
 * Created by giro on 2014.12.15..
 * parses the given XML file and sorts it to the right class of playable items
 */
public class ParseXML {
    private XmlPullParserFactory factory;
    private XmlPullParser xpp;
    private AudioFiles audioFiles = null;
    private VideoFiles videoFiles = null;
    private RomFiles romFiles = null;
    private List<PlaylistItems> playlistItemList = new ArrayList<>();
    private AudioTags audioTags = null;
    private VideoTags videoTags = null;
    private RomTags romTags = null;

    ParseXML() throws XmlPullParserException {
        factory = XmlPullParserFactory.newInstance();
        factory.setNamespaceAware(true);
        xpp = factory.newPullParser();
    }

    public List<PlaylistItems> getPlaylistItems(AsyncTask<Object, Void, String> xmlResponse) throws XmlPullParserException, IOException, ExecutionException, InterruptedException {
        String result = xmlResponse.get();
        result = result.substring(14);

        xpp.setInput( new StringReader(result));

        int eventType = xpp.getEventType();

        while (eventType != XmlPullParser.END_DOCUMENT) {
            switch (eventType) {
                case XmlPullParser.START_TAG:
                    if (xpp.getName().equals("item")) {
                        for (int i = 1; i < xpp.getAttributeCount(); i++) {
                            if (xpp.getAttributeName(i).equals("type")) {
                                if (xpp.getAttributeValue(i).equals("100")) {
                                    audioFiles = new AudioFiles();
                                    audioFiles.setType(Integer.valueOf(xpp.getAttributeValue(i)));
                                } else if (xpp.getAttributeValue(i).equals("200")) {
                                    romFiles = new RomFiles();
                                    romFiles.setType(Integer.valueOf(xpp.getAttributeValue(i)));
                                } else if (xpp.getAttributeValue(i).equals("201")) {
                                    romFiles = new GBRomsFiles();
                                    romFiles.setType(Integer.valueOf(xpp.getAttributeValue(i)));
                                } else if (xpp.getAttributeValue(i).equals("202")) {
                                    romFiles = new GBRomsFiles();
                                    romFiles.setType(Integer.valueOf(xpp.getAttributeValue(i)));
                                } else if (xpp.getAttributeValue(i).equals("300")) {
                                    videoFiles = new VideoFiles();
                                    videoFiles.setType(Integer.valueOf(xpp.getAttributeValue(i)));
                                }
                            }
                            if (xpp.getAttributeName(i).equals("label")) {
                                if (audioFiles != null) {
                                    audioFiles.setLabel(xpp.getAttributeValue(i));
                                } else if (videoFiles != null) {
                                    videoFiles.setLabel(xpp.getAttributeValue(i));
                                } else if (romFiles != null) {
                                    romFiles.setLabel(xpp.getAttributeValue(i));
                                }
                            }
                        }
                        if (xpp.getAttributeName(0).equals("id")) {
                            if (audioFiles != null) {
                                audioFiles.setID(Integer.valueOf(xpp.getAttributeValue(0)));
                            } else if (videoFiles != null) {
                                videoFiles.setID(Integer.valueOf(xpp.getAttributeValue(0)));
                            } else if (romFiles != null) {
                                romFiles.setID(Integer.valueOf(xpp.getAttributeValue(0)));
                            }
                        }
                    }
                    break;
                case XmlPullParser.TEXT:
                    break;
                case XmlPullParser.END_TAG:
                    break;
                default:
                    break;
            }
            if (audioFiles != null) {
                playlistItemList.add(audioFiles);
                audioFiles = null;
            } else if (videoFiles != null) {
                playlistItemList.add(videoFiles);
                videoFiles = null;
            } else if (romFiles != null) {
                playlistItemList.add(romFiles);
                romFiles = null;
            }
            eventType = xpp.next();
        }
        Log.d("parseXML", "playlist might be empty: " + playlistItemList.isEmpty());
        if (!playlistItemList.isEmpty()) {
            playlistItemList.get(0).setNextID(playlistItemList.get(1).getID());
            playlistItemList.get(playlistItemList.size() - 1).setPrevID(playlistItemList.get(playlistItemList.size() - 2).getID());
            for (int x = 1; x < playlistItemList.size() - 1; x++) {
                playlistItemList.get(x).setNextID(playlistItemList.get(x + 1).getID());
                playlistItemList.get(x).setPrevID(playlistItemList.get(x - 1).getID());
            }
        }
        return playlistItemList;
    }

    public int getPrevID(AsyncTask<Object, Void, String> xmlResponse, int id) throws XmlPullParserException, ExecutionException, InterruptedException, IOException {
        String result = xmlResponse.get();
        Log.d("parseXML", "XML is: " + result);
        result = result.substring(14);
        Log.d("parseXML", "XML is: " + result);
        xpp.setInput( new StringReader(result));

        int eventType = xpp.getEventType();

        while (eventType != XmlPullParser.END_DOCUMENT) {
            switch (eventType) {
                case XmlPullParser.START_TAG:
                    if (xpp.getName().equals("item")) {
                        if (xpp.getAttributeName(0).equals("id")) {
                            Log.d("'parseXML", "id is: " + xpp.getAttributeValue(0));
                            audioFiles = new AudioFiles();
                            audioFiles.setID(Integer.valueOf(xpp.getAttributeValue(0)));
                        }
                    }
                    break;
                case XmlPullParser.TEXT:
                    break;
                case XmlPullParser.END_TAG:
                    break;
                default:
                    break;
            }
            if (audioFiles != null) {
                playlistItemList.add(audioFiles);
                audioFiles = null;
            }
            eventType = xpp.next();
        }
        if (!playlistItemList.isEmpty()) {
            String classTypeOfFile = playlistItemList.get(0).getClass().getName();
            Log.d("parseXML", "Class of File is: " + classTypeOfFile);
            if (classTypeOfFile.contains("AudioFiles")) {
                Log.d("parseXML", "first next id is: " + playlistItemList.get(1).getID());
                playlistItemList.get(0).setNextID(playlistItemList.get(1).getID());
                Log.d("parseXML", "last prev ID is: " + playlistItemList.get(playlistItemList.size() - 1).getID());
                playlistItemList.get(playlistItemList.size()-1).setPrevID(playlistItemList.get(playlistItemList.size()-2).getID());
                for (int x = 1; x < playlistItemList.size()-1; x++) {
                    Log.d("parseXML", "nextID is: " + playlistItemList.get(x+1).getID());
                    Log.d("parseXML", "prevID is: " + playlistItemList.get(x-1).getID());
                    playlistItemList.get(x).setNextID(playlistItemList.get(x+1).getID());
                    playlistItemList.get(x).setPrevID(playlistItemList.get(x-1).getID());
                }
            }
        }
        Log.d("parseXML", "id is: " + id + "    size is: " + playlistItemList.size());
        for (int x = 0; x < playlistItemList.size(); x++) {
            Log.d("parseXML", "parsed ID is: " + playlistItemList.get(x).getID());
            if (playlistItemList.get(x).getID() == id) {
            Log.d("parseXML", "prevID is: "+ playlistItemList.get(x).getPrevID());
                return playlistItemList.get(x).getPrevID();
            }
        }
        return 0;
    }

    public int getNextID(AsyncTask<Object, Void, String> xmlResponse, int id) throws XmlPullParserException, ExecutionException, InterruptedException, IOException {
        String result = xmlResponse.get();
        Log.d("parseXML", "XML is: " + result);
        result = result.substring(14);
        Log.d("parseXML", "XML is: " + result);
        xpp.setInput( new StringReader(result));

        int eventType = xpp.getEventType();

        while (eventType != XmlPullParser.END_DOCUMENT) {
            switch (eventType) {
                case XmlPullParser.START_TAG:
                    if (xpp.getName().equals("item")) {
                        if (xpp.getAttributeName(0).equals("id")) {
                            Log.d("'parseXML", "id is: " + xpp.getAttributeValue(0));
                            audioFiles = new AudioFiles();
                            audioFiles.setID(Integer.valueOf(xpp.getAttributeValue(0)));
                        }
                    }
                    break;
                case XmlPullParser.TEXT:
                    break;
                case XmlPullParser.END_TAG:
                    break;
                default:
                    break;
            }
            if (audioFiles != null) {
                playlistItemList.add(audioFiles);
                audioFiles = null;
            }
            eventType = xpp.next();
        }
        if (!playlistItemList.isEmpty()) {
            String classTypeOfFile = playlistItemList.get(0).getClass().getName();
            Log.d("parseXML", "Class of File is: " + classTypeOfFile);
            if (classTypeOfFile.contains("AudioFiles")) {
                Log.d("parseXML", "first next id is: " + playlistItemList.get(1).getID());
                playlistItemList.get(0).setNextID(playlistItemList.get(1).getID());
                Log.d("parseXML", "last prev ID is: "+ playlistItemList.get(playlistItemList.size() - 1).getID());
                playlistItemList.get(playlistItemList.size()-1).setPrevID(playlistItemList.get(playlistItemList.size()-2).getID());
                for (int x = 1; x < playlistItemList.size()-1; x++) {
                    Log.d("parseXML", "nextID is: " + playlistItemList.get(x+1).getID());
                    Log.d("parseXML", "prevID is: " + playlistItemList.get(x-1).getID());
                    playlistItemList.get(x).setNextID(playlistItemList.get(x+1).getID());
                    playlistItemList.get(x).setPrevID(playlistItemList.get(x-1).getID());
                }
            }
        }
        Log.d("parseXML", "id is: " + id + "    size is: " + playlistItemList.size());
        for (int x = 0; x < playlistItemList.size(); x++) {
            Log.d("parseXML", "parsed ID is: " + playlistItemList.get(x).getID());
            if (playlistItemList.get(x).getID() == id) {
                Log.d("parseXML", "prevID is: "+ playlistItemList.get(x).getPrevID());
                return playlistItemList.get(x).getNextID();
            }
        }
        return 0;
    }

    public Tags getTagInfo(AsyncTask<Object, Void, String> xmlResponse) throws XmlPullParserException, IOException, ExecutionException, InterruptedException {
        String result = xmlResponse.get();
        result = result.substring(14);

        xpp.setInput(new StringReader(result));

        int eventType = xpp.getEventType();

        String tagname = null;
        while (eventType != XmlPullParser.END_DOCUMENT) {
            switch (eventType) {
                case XmlPullParser.START_TAG:
                    tagname = xpp.getName();
                    if (tagname.equals("item")) {
                        for (int x = 0; x < xpp.getAttributeCount(); x++) {
                            if (xpp.getAttributeName(x).equals("type")) {
                                if (Integer.valueOf(xpp.getAttributeValue(x)) == 100) {
                                    audioTags = new AudioTags();
                                    break;
                                } else if (Integer.valueOf(xpp.getAttributeValue(x)) == 300) {
                                    videoTags = new VideoTags();
                                    videoTags.setTitle(xpp.getAttributeValue("", "label"));
                                    break;
                                } else if (Integer.valueOf(xpp.getAttributeValue(x)) == 200) {
                                    romTags = new RomTags();
                                    break;
                                } else if (Integer.valueOf(xpp.getAttributeValue(x)) == 201) {
                                    romTags = new RomTags();
                                    break;
                                } else if (Integer.valueOf(xpp.getAttributeValue(x)) == 202) {
                                    romTags = new RomTags();
                                    break;
                                } else {
                                    return null;
                                }
                            }
                        }

                    }
                    break;
                case XmlPullParser.TEXT:
                    if (tagname == null) {
                        break;
                    }
                    if (tagname.equals("title")) {
                        String title = xpp.getText();
                        if (title != null) {
                            if (audioTags != null) {
                                audioTags.setTitle(title);
                            } else if (videoTags != null) {
                                videoTags.setTitle(title);
                            } else if (romTags!= null) {
                                romTags.setTitle(title);
                            }
                        }
                        break;
                    }
                    if (tagname.equals("artist")) {
                        String artist = xpp.getText();
                        if (artist != null) {
                            if (audioTags != null) {
                                audioTags.setArtist(artist);
                            } else if (videoTags != null) {
                                videoTags.setArtist(artist);
                            } else if (romTags != null) {
                                romTags.setArtist(artist);
                            }
                        }
                        break;
                    }
                    if (tagname.equals("album")) {
                        String album = xpp.getText();
                        if (album != null) {
                            if (audioTags != null) {
                                audioTags.setAlbum(album);
                            } else if (videoTags != null) {
                                videoTags.setAlbum(album);
                            } else if (romTags != null) {
                                romTags.setAlbum(album);
                            }
                        }
                        break;
                    }
                    if (tagname.equals("year")) {
                        String year = xpp.getText();
                        if (year != null) {
                            if (audioTags != null) {
                                audioTags.setYear(year);
                            } else if (videoTags != null) {
                                videoTags.setYear(year);
                            } else if (romTags != null) {
                                romTags.setYear(year);
                            }
                        }
                        break;
                    }
                    if (tagname.equals("comment")) {
                        String comment = xpp.getText();
                        if (comment != null) {
                            if (audioTags != null) {
                                audioTags.setComment(comment);
                            } else if (videoTags != null) {
                                videoTags.setComment(comment);
                            } else if (romTags != null) {
                                romTags.setComment(comment);
                            }
                        }
                        break;
                    }
                    if (tagname.equals("track")) {
                        String track = xpp.getText();
                        if (track != null) {
                            if (audioTags != null) {
                                audioTags.setTrack(track);
                            } else if (videoTags != null) {
                                videoTags.setTrack(track);
                            } else if (romTags != null) {
                                romTags.setTrack(track);
                            }
                        }
                        break;
                    }
                    if (tagname.equals("genre")) {
                        String genre = xpp.getText();
                        if (genre != null) {
                            if (audioTags != null) {
                                audioTags.setGenre(genre);
                            } else if (videoTags != null) {
                                videoTags.setGenre(genre);
                            } else if (romTags != null)
                                romTags.setGenre(genre);
                        }
                        break;
                    }
                    if (tagname.equals("bitrate")) {
                        String bitrate = xpp.getText();
                        if (bitrate != null) {
                            if (audioTags != null) {
                                audioTags.setBitrate(bitrate);
                            } else if (videoTags != null) {
                                videoTags.setBitrate(bitrate);
                            } else if (romTags != null)
                                romTags.setBitrate(bitrate);
                        }
                        break;
                    }
                    if (tagname.equals("samplerate")) {
                        String samplerate = xpp.getText();
                        if (samplerate != null) {
                            if (audioTags != null) {
                                audioTags.setSamplerate(samplerate);
                            } else if (videoTags != null) {
                                videoTags.setSamplerate(samplerate);
                            } else if (romTags != null) {
                                romTags.setSamplerate(samplerate);
                            }
                        }
                        break;
                    }
                    if (tagname.equals("channels")) {
                        String channels = xpp.getText();
                        if (channels != null) {
                            if (audioTags != null) {
                                audioTags.setChannels(channels);
                            } else if (videoTags != null) {
                                videoTags.setChannels(channels);
                            } else if (romTags != null) {
                                romTags.setChannels(channels);
                            }
                        }
                        break;
                    }
                    if (tagname.equals("length")) {
                        String length = xpp.getText();
                        if (length != null) {
                            if (audioTags != null) {
                                audioTags.setLength(length);
                            } else if (videoTags != null) {
                                videoTags.setLength(length);
                            } else if (romTags != null) {
                                romTags.setLength(length);
                            }
                        }
                        break;
                    }
                    break;
                case XmlPullParser.END_TAG:
                    break;
                default:
                    break;
            }
            eventType = xpp.next();
        }

        if (audioTags != null) {
            return audioTags;
        } else if (videoTags != null) {
            return videoTags;
        } else if (romTags != null) {
            return romTags;
        } else {
            return null;
        }
    }

    public static boolean getLoginStatus(AsyncTask<Object, Void, String> stat) throws ExecutionException, InterruptedException {
        if (!stat.get().isEmpty()) {
            String returnCode = stat.get().substring(8, 11);

            if (!isInteger(returnCode)) {
                return false;
            }

            switch (Integer.valueOf(returnCode)) {
                case 200:
                    return true;
                case 401:
                    return false;
                case 504:
                    return false;
                default:
                    return false;
            }
        }
        return false;
    }

    public static boolean getCTRLReturnCodeStatus(AsyncTask<Object, Void, String> stat) throws ExecutionException, InterruptedException {
        if (!stat.get().isEmpty()) {
            String returnCode = stat.get().substring(8, 11);

            if (!isInteger(returnCode)) {
                return false;
            }

            switch (Integer.valueOf(returnCode)) {
                case 200:
                    return true;
                case 401:
                    return false;
                case 500:
                    return false;
                case 504:
                    return false;
                default:
                    return false;
                }
        }
        return false;
    }

    public static Boolean getPlayReturnCodeStatus(AsyncTask<Object, Void, String> stat) throws ExecutionException, InterruptedException {
        String returnCode = stat.get().substring(8, 11);

        if (!isInteger(returnCode)) {
            return false;
        }

        switch (Integer.valueOf(returnCode)) {
            case 200:
                return true;
            case 401:
                return false;
            case 402:
                return false;
            case 500:
                return false;
            case 501:
                return true;
            case 502:
                return false;
            case 504:
                return false;
            default:
                return false;
            }
    }

    private static boolean isInteger(String s) {
        try {
            Integer.parseInt(s);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
