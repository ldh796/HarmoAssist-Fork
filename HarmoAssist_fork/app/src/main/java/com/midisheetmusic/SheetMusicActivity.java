/*
 * Copyright (c) 2011-2012 Madhav Vaidyanathan
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License version 2.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 */

package com.midisheetmusic;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.drawerlayout.widget.DrawerLayout;

import com.midisheetmusic.drawerItems.ExpandableSwitchDrawerItem;
import com.midisheetmusic.sheets.ClefSymbol;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.holder.StringHolder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.ExpandableDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.SecondarySwitchDrawerItem;
import com.mikepenz.materialdrawer.model.SwitchDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.zip.CRC32;

import kr.sweetcase.harmoassist.MusicInfoFromSheetActivity;
import kr.sweetcase.harmoassist.R;
import kr.sweetcase.harmoassist.SheetRedirectionActivity;
import kr.sweetcase.harmoassist.StatisticActivity;
import kr.sweetcase.harmoassist.dialogs.sheettechnicdialog.BasicTechnicSelectorDialog;
import kr.sweetcase.harmoassist.listMaterials.Music;

/**
 * SheetMusicActivity is the main activity. The main components are:
 * <ul>
 *  <li> MidiPlayer : The buttons and speed bar at the top.
 *  <li> Piano : For highlighting the piano notes during playback.
 *  <li> SheetMusic : For highlighting the sheet music notes during playback.
 */
public class SheetMusicActivity extends MidiHandlingActivity implements SheetMusic.SheetMusicRequestListener {

    public static final String MidiTitleID = "MidiTitleID";
    public static final int settingsRequestCode = 1;
    public static final int ID_LOOP_ENABLE = 10;
    public static final int ID_LOOP_START = 11;
    public static final int ID_LOOP_END = 12;

    private MidiPlayer player;   /* The play/stop/rewind toolbar */
    private Piano piano;         /* The piano at the top */
    private SheetMusic sheet;    /* The sheet music */
    private LinearLayout layout; /* The layout */
    private MidiFile midifile;   /* The midi file to play */
    private MidiOptions options; /* The options for sheet music and sound */
    private long midiCRC;        /* CRC of the midi bytes */
    private Drawer drawer;

    // 음악 정보
    private Music musicInfoData;

     /** Create this SheetMusicActivity.
      * The Intent should have two parameters:
      * - data: The uri of the midi file to open.
      * - MidiTitleID: The title of the song (String)
      */
    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);


        // 데이터 갖고오기
        musicInfoData = (Music)getIntent().getSerializableExtra("music_info");

        // Hide the navigation bar before the views are laid out
        hideSystemUI();

        setContentView(R.layout.sheet_music_layout);

        // Keep screen on

        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        ClefSymbol.LoadImages(this);
        TimeSigSymbol.LoadImages(this);

        // Parse the MidiFile from the raw bytes
        Uri uri = this.getIntent().getData();
        if (uri == null) {
            this.finish();
            return;
        }
        String title = this.getIntent().getStringExtra(MidiTitleID);
        if (title == null) {
            title = uri.getLastPathSegment();
        }
        FileUri file = new FileUri(uri, title);
        this.setTitle("MidiSheetMusic: " + title);
        byte[] data;
        try {
            data = file.getData(this);
            midifile = new MidiFile(data, title);
        }
        catch (MidiFileException e) {
            this.finish();
            return;
        }

        // Initialize the settings (MidiOptions).
        // If previous settings have been saved, use those
        options = new MidiOptions(midifile);
        CRC32 crc = new CRC32();
        crc.update(data);
        midiCRC = crc.getValue();
        SharedPreferences settings = getPreferences(0);
        options.scrollVert = settings.getBoolean("scrollVert", false);
        options.shade1Color = settings.getInt("shade1Color", options.shade1Color);
        options.shade2Color = settings.getInt("shade2Color", options.shade2Color);
        options.showPiano = settings.getBoolean("showPiano", false); // TODO 건반 관련

        String json = settings.getString("" + midiCRC, null);
        MidiOptions savedOptions = MidiOptions.fromJson(json);
        if (savedOptions != null) {
            options.merge(savedOptions);
        }
        createViews();
    }

    /* Create the MidiPlayer and Piano views */
    void createViews() {
        /**
         * 1. 악보 정보
         * 2. 악보 분석
         * 3. 미디파일 추출
         * 4. 저장
         * 5. 화음 배치 (마디 선택 시)
         */
        layout = findViewById(R.id.sheet_content);

        // 악보 정보
        PrimaryDrawerItem infoMusicBtn = new PrimaryDrawerItem()
                .withName("악보 정보")
                .withOnDrawerItemClickListener((view, i, item) -> {
                    Intent intent = new Intent(this, MusicInfoFromSheetActivity.class);
                    intent.putExtra("music_info", musicInfoData);
                    this.startActivity(intent);
                    return true;
                });

        PrimaryDrawerItem statisticBtn = new PrimaryDrawerItem()
                .withName("악보 분석")
                .withOnDrawerItemClickListener((view, i, item) -> {
                    // TODO 백엔드 데이터를 갖고 와야 한다.
                    Intent intent = new Intent(this, StatisticActivity.class);
                    intent.putExtra("statistics_info", musicInfoData);
                    this.startActivity(intent);
                    return true;
                });

        PrimaryDrawerItem shareMidiBtn = new PrimaryDrawerItem()
                .withName("미디파일 추출")
                .withOnDrawerItemClickListener((view, i, item) -> {
                    // TODO 우선 변경된 미디 데이터 tmp.mid에 업데이트 해야 한다.

                    // 밑에있는건 예시
                    // TODO Nuget 이후 바뀐 정책을 사용해야 함.
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_SEND);
                    intent.setType("application/*");

                    intent.putExtra(Intent.EXTRA_SUBJECT, musicInfoData.getTitle());
                    Log.d("paths", getFileStreamPath("tmp.mid").getAbsolutePath());
                    intent.putExtra(Intent.EXTRA_STREAM, Uri.parse(getFileStreamPath("tmp.mid").getAbsolutePath()));

                    Intent chooser = Intent.createChooser(intent, musicInfoData.getTitle());
                    view.getContext().startActivity(chooser);

                    return true;
                });
        PrimaryDrawerItem saveBtn = new PrimaryDrawerItem()
                .withName("저장")
                .withOnDrawerItemClickListener((view, i, item) -> {
                    // TODO DB에 저장
                    return true;
                });

        /** 화음 배치 하위메뉴 **/
        SecondaryDrawerItem techBasicBtn = new SecondaryDrawerItem()
                .withName("수동 설정")
                .withOnDrawerItemClickListener((view, i, item) -> {
                    // TODO 화음 수동 설정
                    new BasicTechnicSelectorDialog(this).show();
                    return true;
                });
        SecondaryDrawerItem advancedTechBtn = new SecondaryDrawerItem()
                .withName("화음 배치");
        SecondaryDrawerItem customTechBtn = new SecondaryDrawerItem()
                .withName("커스텀 배치");

        /** 화음 배치 메뉴 **/
        ExpandableDrawerItem techBtn = new ExpandableDrawerItem()
                .withName("화음 베치")
                .withSubItems(techBasicBtn, advancedTechBtn, customTechBtn);


        // Drawer
        drawer = new DrawerBuilder()
                .withActivity(this)
                .withInnerShadow(true)
                .addDrawerItems(
                        infoMusicBtn,
                        statisticBtn,
                        shareMidiBtn,
                        saveBtn,
                        new DividerDrawerItem(),
                        techBtn
                )
                .withOnDrawerItemClickListener((view, i, item) -> drawerItemClickListener(item))
                .withDrawerGravity(Gravity.RIGHT)
                .build();

        // Make sure that the view extends over the navigation buttons area
        drawer.getDrawerLayout().setFitsSystemWindows(false);
        // Lock the drawer so swiping doesn't open it
        drawer.getDrawerLayout().setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

        player = new MidiPlayer(this);
        player.setDrawer(drawer);
        layout.addView(player);

        piano = new Piano(this);
        //layout.addView(piano);

        player.SetPiano(piano);
        layout.requestLayout();

        player.setSheetUpdateRequestListener(() -> createSheetMusic(options));
        this.midifile.recalculateEvents();
        createSheetMusic(options);
    }

    /** Create the SheetMusic view with the given options */
    private void 
    createSheetMusic(MidiOptions options) {
        if (sheet != null) {
            layout.removeView(sheet);
        }

        piano.setVisibility(options.showPiano ? View.VISIBLE : View.GONE);
        sheet = new SheetMusic(this);
        sheet.init(midifile, options, this);
        sheet.setPlayer(player);
        layout.addView(sheet);
        piano.SetMidiFile(midifile, options, player);
        piano.SetShadeColors(options.shade1Color, options.shade2Color);

        player.SetMidiFile(midifile, options, sheet);
        player.updateToolbarButtons();
        layout.requestLayout();
        sheet.draw();
    }

    private void
    updateSheetMusic(MidiOptions options) {
        sheet.reinit(options);
        player.updateToolbarButtons();
        layout.requestLayout();
        sheet.draw();
    }


    /** Always display this activity in landscape mode. */
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }


    /** Create a string list of the numbers between listStart and listEnd (inclusive) */
    private String[] makeStringList(int listStart, int listEnd) {
        String[] list = new String[listEnd];
        for (int i = 0; i < list.length; i++) {
            list[i] = Integer.toString(i + listStart);
        }
        return list;
    }


    /** TODO Handle clicks on the drawer menu */
    public boolean drawerItemClickListener(IDrawerItem item) {
        switch ((int)item.getIdentifier()) {
            case R.id.sheet_info_menu_btn:
                break;
        }
        return true;
    }


    /** To change the sheet music options, start the SettingsActivity.
     *  Pass the current MidiOptions as a parameter to the Intent.
     *  Also pass the 'default' MidiOptions as a parameter to the Intent.
     *  When the SettingsActivity has finished, the onActivityResult()
     *  method will be called.
     */
    private void changeSettings() {
        MidiOptions defaultOptions = new MidiOptions(midifile);
        /*
        Intent intent = new Intent(this, SettingsActivity.class);
        intent.putExtra(SettingsActivity.settingsID, options);
        intent.putExtra(SettingsActivity.defaultSettingsID, defaultOptions);
        startActivityForResult(intent, settingsRequestCode);
         */
    }


    /* Show the "Save As Images" dialog */
    private void showSaveImagesDialog() {
         LayoutInflater inflator = LayoutInflater.from(this);
         final View dialogView= inflator.inflate(R.layout.save_images_dialog, layout, false);
         final EditText filenameView = dialogView.findViewById(R.id.save_images_filename);
         filenameView.setText(midifile.getFileName().replace("_", " ") );
         AlertDialog.Builder builder = new AlertDialog.Builder(this);
         builder.setTitle(R.string.save_images_str);
         builder.setView(dialogView);
         builder.setPositiveButton("OK",
                 (builder1, whichButton) -> saveAsImages(filenameView.getText().toString()));
         builder.setNegativeButton("Cancel",
                 (builder12, whichButton) -> {
         });
         AlertDialog dialog = builder.create();
         dialog.show();
    }


    /* Save the current sheet music as PNG images. */
    private void saveAsImages(String name) {
        String filename = name;
        try {
            filename = URLEncoder.encode(name, "utf-8");
        }
        catch (UnsupportedEncodingException e) {
            Toast.makeText(this, "Error: unsupported encoding in filename", Toast.LENGTH_SHORT).show();
        }
        if (!options.scrollVert) {
            options.scrollVert = true;
            createSheetMusic(options);
        }
        try {
            int numpages = sheet.GetTotalPages();
            for (int page = 1; page <= numpages; page++) {
                Bitmap image= Bitmap.createBitmap(SheetMusic.PageWidth + 40, SheetMusic.PageHeight + 40, Bitmap.Config.ARGB_8888);
                Canvas imageCanvas = new Canvas(image);
                sheet.DrawPage(imageCanvas, page);
                File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES + "/MidiSheetMusic");
                File file = new File(path, "" + filename + page + ".png");
                path.mkdirs();
                OutputStream stream = new FileOutputStream(file);
                image.compress(Bitmap.CompressFormat.PNG, 0, stream);
                stream.close();

                // Inform the media scanner about the file
                MediaScannerConnection.scanFile(this, new String[] { file.toString() }, null, null);
            }
        }
        catch (IOException e) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Error saving image to file " + Environment.DIRECTORY_PICTURES + "/MidiSheetMusic/" + filename  + ".png");
            builder.setCancelable(false);
            builder.setPositiveButton("OK", (dialog, id) -> { });
            AlertDialog alert = builder.create();
            alert.show();
        }
        catch (NullPointerException e) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Ran out of memory while saving image to file " + Environment.DIRECTORY_PICTURES + "/MidiSheetMusic/" + filename  + ".png");
            builder.setCancelable(false);
            builder.setPositiveButton("OK", (dialog, id) -> {});
            AlertDialog alert = builder.create();
            alert.show();
        }
    }


    /** Show the HTML help screen. */
    private void showHelp() {
        /*
        Intent intent = new Intent(this, HelpActivity.class);
        startActivity(intent);
         */
    }

    /** Save the options in the SharedPreferences */
    private void saveOptions() {
        SharedPreferences.Editor editor = getPreferences(0).edit();
        editor.putBoolean("scrollVert", options.scrollVert);
        editor.putInt("shade1Color", options.shade1Color);
        editor.putInt("shade2Color", options.shade2Color);
        editor.putBoolean("showPiano", options.showPiano);
        for (int i = 0; i < options.noteColors.length; i++) {
            editor.putInt("noteColor" + i, options.noteColors[i]);
        }
        String json = options.toJson();
        if (json != null) {
            editor.putString("" + midiCRC, json);
        }
        editor.apply();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        saveOptions();
    }

    /** This is the callback when the SettingsActivity is finished.
     *  Get the modified MidiOptions (passed as a parameter in the Intent).
     *  Save the MidiOptions.  The key is the CRC checksum of the midi data,
     *  and the value is a JSON dump of the MidiOptions.
     *  Finally, re-create the SheetMusic View with the new options.
     */
    @Override
    protected void onActivityResult (int requestCode, int resultCode, Intent intent) {
        /*
        if (requestCode != settingsRequestCode) {
            return;
        }
        options = (MidiOptions) 
            intent.getSerializableExtra(SettingsActivity.settingsID);

        // Check whether the default instruments have changed.
        for (int i = 0; i < options.instruments.length; i++) {
            if (options.instruments[i] !=  
                midifile.getTracks().get(i).getInstrument()) {
                options.useDefaultInstruments = false;
            }
        }

        saveOptions();

        // Recreate the sheet music with the new options
        createSheetMusic(options);

         */
    }

    /** When this activity resumes, redraw all the views */
    @Override
    protected void onResume() {
        super.onResume();
        layout.requestLayout();
        player.invalidate();
        piano.invalidate();
        if (sheet != null) {
            sheet.invalidate();
        }
        layout.requestLayout();
    }

    /** When this activity pauses, stop the music */
    @Override
    protected void onPause() {
        if (player != null) {
            player.Pause();
        }
        super.onPause();
    }

    @Override
    void OnMidiDeviceStatus(boolean connected) {
        player.OnMidiDeviceStatus(connected);
    }

    @Override
    void OnMidiNote(int note, boolean pressed) {
        player.OnMidiNote(note, pressed);
    }

    /************************** Hide navigation buttons **************************/

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            hideSystemUI();
        }
    }

    private void hideSystemUI() {
        // Enables sticky immersive mode.
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        // Set the content to appear under the system bars so that the
                        // content doesn't resize when the system bars hide and show.
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        // Hide the nav bar and status bar
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN);
    }

    @Override
    public void onNoteAddRequest(int trackNum, MidiNote midiNote) {
        Log.d("SheetMusicActivity", String.format("[%d] onNoteAddRequest() called: %s", trackNum, midiNote.toString()));
        this.midifile.getTracks().get(trackNum).AddNote(midiNote);
        createSheetMusic(options);
        this.midifile.recalculateEvents();
    }

    @Override
    public void onRefreshRequest() {
        createSheetMusic(options);
        this.midifile.recalculateEvents();
    }
}

