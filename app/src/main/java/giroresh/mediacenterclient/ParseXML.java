package giroresh.mediacenterclient;

import android.os.AsyncTask;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import giroresh.mediacenterclient.playlistItems.MCCException.NoTagsException;
import giroresh.mediacenterclient.playlistItems.filetypes.AudioFiles;
import giroresh.mediacenterclient.playlistItems.filetypes.GBRomsFiles;
import giroresh.mediacenterclient.playlistItems.filetypes.PlaylistItems;
import giroresh.mediacenterclient.playlistItems.filetypes.RomFiles;
import giroresh.mediacenterclient.playlistItems.filetypes.VideoFiles;
import giroresh.mediacenterclient.playlistItems.tags.AudioTags;
import giroresh.mediacenterclient.playlistItems.tags.Tags;
import giroresh.mediacenterclient.playlistItems.tags.VideoTags;

/**
 * Created by giro on 2014.12.15..
 * parses the given XML file and sorts it to the right class of playable items
 */
public class ParseXML {
    private XmlPullParser xpp;
    private AudioFiles audioFiles = null;
    private VideoFiles videoFiles = null;
    private RomFiles romFiles = null;
    private List<PlaylistItems> playlistItemList = new ArrayList<>();
    private AudioTags audioTags = null;
    private VideoTags videoTags = null;

    ParseXML() throws XmlPullParserException {
        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
        factory.setNamespaceAware(true);
        xpp = factory.newPullParser();
    }

    private List<PlaylistItems> getAllFiles(AsyncTask<Object, Void, String> xmlResponse) throws ExecutionException, InterruptedException, XmlPullParserException, IOException {
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

    public List<PlaylistItems> getPlaylistItems(AsyncTask<Object, Void, String> xmlResponse) throws XmlPullParserException, IOException, ExecutionException, InterruptedException {
        return getAllFiles(xmlResponse);
    }

    public int getPrevID(AsyncTask<Object, Void, String> xmlResponse, int id) throws XmlPullParserException, ExecutionException, InterruptedException, IOException {
        playlistItemList = getAllFiles(xmlResponse);
        for (int x = 0; x < playlistItemList.size(); x++) {
            if (playlistItemList.get(x).getID() == id) {
                return playlistItemList.get(x).getPrevID();
            }
        }
        return 0;
    }

    public int getNextID(AsyncTask<Object, Void, String> xmlResponse, int id) throws XmlPullParserException, ExecutionException, InterruptedException, IOException {
        playlistItemList = getAllFiles(xmlResponse);
        for (int x = 0; x < playlistItemList.size(); x++) {
            if (playlistItemList.get(x).getID() == id) {
                return playlistItemList.get(x).getNextID();
            }
        }
        return 0;
    }

    public String getTitleToPlay(AsyncTask<Object, Void, String> xmlResponse, int id) throws XmlPullParserException, ExecutionException, InterruptedException, IOException {
        playlistItemList = getAllFiles(xmlResponse);
        for (int x = 0; x < playlistItemList.size(); x++) {
            if (playlistItemList.get(x).getID() == id) {
                return playlistItemList.get(x).getLabel();
            }
        }
        return "";
    }

    public Tags getTagInfo(AsyncTask<Object, Void, String> xmlResponse) throws XmlPullParserException, IOException, ExecutionException, InterruptedException, NoTagsException {
        String result = xmlResponse.get();

        int resLength = result.length()>25 ? 25:result.length();

        if (getStatusOfListCMD(result.substring(0, resLength))) {

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
                                }
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
                                }
                            }
                            break;
                        }
                        if (tagname.equals("samplerate")) {
                            String samplerate = xpp.getText();
                            if (samplerate != null) {
                                if (audioTags != null) {
                                    audioTags.setSample(samplerate);
                                } else if (videoTags != null) {
                                    videoTags.setSample(samplerate);
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
            } else {
                return null;
            }
        } else {
            throw new NoTagsException("No Tags");
        }
    }

    /**
     *
     * @param result is the xml response of the server that gets checked for OK  messages
     * @return true if OK found
     */
    private Boolean getStatusOfListCMD(String result) {
        return result.contains("200");
    }

    public Boolean getStatus(AsyncTask<Object, Void, String> stat) throws ExecutionException, InterruptedException {
        if (stat.get().isEmpty()) {
            return false;
        } else {
            String returnCode = stat.get().substring(8, 11);

            if (isNotInteger(returnCode)) {
                return false;
            }

            return returnCode.contains("200");
        }
    }

    private boolean isNotInteger(String s) {
        try {
            Integer.parseInt(s);
            return false;
        } catch (NumberFormatException e) {
            return true;
        }
    }

    public String getServerStatus(AsyncTask<Object, Void, String> xmlResponse) throws ExecutionException, InterruptedException, XmlPullParserException, IOException {
        String result = xmlResponse.get();
        result = result.substring(14);

        xpp.setInput(new StringReader(result));

        int eventType = xpp.getEventType();
        String serverStatusStr = "";
        while (eventType != XmlPullParser.END_DOCUMENT) {
            switch (eventType) {
                case XmlPullParser.START_TAG:
                    if (xpp.getName().equals("items")) {
                        for (int i = 0; i < xpp.getAttributeCount(); i++) {
                            if (xpp.getAttributeName(i).equals("version")) {
                                serverStatusStr += "" + xpp.getAttributeValue(i);
                            } else if (xpp.getAttributeName(i).equals("size")) {
                                serverStatusStr += "\t" +  xpp.getAttributeValue(i) + "\n";
                            }
                        }
                    }
                    if (xpp.getName().equals("type")) {
                        for (int i = 0; i < xpp.getAttributeCount(); i++) {
                            if (xpp.getAttributeName(i).equals("id")) {
                                serverStatusStr += "" + xpp.getAttributeValue(i);
                            } else if (xpp.getAttributeName(i).equals("name")) {
                                serverStatusStr += "\t" + xpp.getAttributeValue(i) + "\n";
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
            eventType = xpp.next();
        }
        return serverStatusStr;
    }
}
