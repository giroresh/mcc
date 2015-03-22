package giroresh.mediacenterclient.helper;

import android.content.res.Resources;
import android.util.Log;

import giroresh.mediacenterclient.R;

/**
 * Does the jobs which are equal to all Fragments
 * For now:
 *      1. getting the tag info string
 * Created by giro on 2015.03.22..
 */
public class MCCFragHelper {
    public static String getMultiLangString(Resources resources, String[] tagInfo) {
        String tagInfoMultiLang = resources.getString(R.string.tagNoInfo);
        for (String aTagInfo : tagInfo) {
            if (aTagInfo.startsWith("title")) {
                Log.d("AudioFrag", "tagInfo is: " + aTagInfo);
                tagInfoMultiLang = resources.getString(R.string.tagTitle) + aTagInfo.substring(aTagInfo.indexOf('\t')) + "\n";
            } else if (aTagInfo.startsWith("album")) {
                tagInfoMultiLang += resources.getString(R.string.tagAlbum) + aTagInfo.substring(aTagInfo.indexOf('\t')) + "\n";
            } else if (aTagInfo.startsWith("artist")) {
                tagInfoMultiLang += resources.getString(R.string.tagArtist) + aTagInfo.substring(aTagInfo.indexOf('\t')) + "\n";
            } else if (aTagInfo.startsWith("genre")) {
                tagInfoMultiLang += resources.getString(R.string.tagGenre) + aTagInfo.substring(aTagInfo.indexOf('\t')) + "\n";
            } else if (aTagInfo.startsWith("track")) {
                tagInfoMultiLang += resources.getString(R.string.tagTrack) + aTagInfo.substring(aTagInfo.indexOf('\t')) + "\n";
            } else if (aTagInfo.startsWith("year")) {
                tagInfoMultiLang += resources.getString(R.string.tagYear) + aTagInfo.substring(aTagInfo.indexOf('\t')) + "\n";
            } else if (aTagInfo.startsWith("length")) {
                tagInfoMultiLang += resources.getString(R.string.tagLength) + aTagInfo.substring(aTagInfo.indexOf('\t')) + "\n";
            } else if (aTagInfo.startsWith("bitrate")) {
                tagInfoMultiLang += resources.getString(R.string.tagBitrate) + aTagInfo.substring(aTagInfo.indexOf('\t')) + "\n";
            } else if (aTagInfo.startsWith("sample")) {
                tagInfoMultiLang += resources.getString(R.string.tagSample) + aTagInfo.substring(aTagInfo.indexOf('\t')) + "\n";
            } else if (aTagInfo.startsWith("channels")) {
                tagInfoMultiLang += resources.getString(R.string.tagChannels) + aTagInfo.substring(aTagInfo.indexOf('\t')) + "\n";
            } else if (aTagInfo.startsWith("comment")) {
                tagInfoMultiLang += resources.getString(R.string.tagComment) + aTagInfo.substring(aTagInfo.indexOf('\t')) + "\n";
            }
        }
        return tagInfoMultiLang.substring(0, tagInfoMultiLang.lastIndexOf("\n"));
    }
}
