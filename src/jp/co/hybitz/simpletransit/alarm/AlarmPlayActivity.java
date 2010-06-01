/**
 * Copyright (C) 2010 Hybitz.co.ltd
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 * 
 */
package jp.co.hybitz.simpletransit.alarm;

import java.io.IOException;

import jp.co.hybitz.simpletransit.Preferences;
import jp.co.hybitz.simpletransit.R;
import jp.co.hybitz.simpletransit.alarm.model.AlarmSoundItem;
import android.app.Activity;
import android.content.ContentUris;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

/**
 * @author ichy <ichylinux@gmail.com>
 */
public class AlarmPlayActivity extends Activity {
    MediaPlayer mediaPlayer;
    Vibrator vibrator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.alarm_play);

        Button button = (Button)findViewById(R.id.alarm_stop);
        button.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });
        
        startAlarm();
    }
    
    @Override
    protected void onDestroy() {
        stopAlarm();
        super.onDestroy();
    }

    private void stopAlarm() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
        }
        
        if (vibrator != null) {
            vibrator.cancel();
        }
    }
    
    private void startAlarm() {
        boolean playSound = ! Preferences.isNoSoundButVibration(this);

        if (playSound) {
            playAudio();
        }
        else {
            vibrate();
        }
    }
    
    private void vibrate() {
        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        vibrator.vibrate(new long[]{0, 500, 500}, 1);
    }
    
    private void playAudio() {
        Log.i("OneTimeAlarm", "starting alarm...");
        
        AlarmSoundItem item = Preferences.getAlarmSoundFile(this);
        if (item == null) {
            Log.w("OneTimeAlarm", "no alarm sound to play. check preferences.");
            return;
        }
        
        try {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
            Uri uri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, item.getId());
            mediaPlayer.setDataSource(this, uri);
            mediaPlayer.setLooping(false);
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (IllegalArgumentException e) {
            Log.e("OneTimeAlarm", e.getMessage(), e);
        } catch (SecurityException e) {
            Log.e("OneTimeAlarm", e.getMessage(), e);
        } catch (IllegalStateException e) {
            Log.e("OneTimeAlarm", e.getMessage(), e);
        } catch (IOException e) {
            Log.e("OneTimeAlarm", e.getMessage(), e);
        }
    }
}
