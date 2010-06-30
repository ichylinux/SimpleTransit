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
import jp.co.hybitz.simpletransit.ResultRenderer;
import jp.co.hybitz.simpletransit.SimpleTransitConst;
import jp.co.hybitz.simpletransit.alarm.model.AlarmSoundItem;
import jp.co.hybitz.simpletransit.db.TransitResultDao;
import jp.co.hybitz.simpletransit.model.SimpleTransitResult;
import jp.co.hybitz.simpletransit.model.TransitItem;
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
import android.widget.TextView;

/**
 * @author ichy <ichylinux@gmail.com>
 */
public class AlarmPlayActivity extends Activity implements SimpleTransitConst {
    private MediaPlayer mediaPlayer;
    private Vibrator vibrator;
    private SimpleTransitResult alarmTransitResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alarm_play);

        Button button = (Button)findViewById(R.id.alarm_stop);
        button.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                stopAlarm(true);
            }
        });

        alarmTransitResult = loadTransitResult();
        if (alarmTransitResult != null) {
            startAlarm(alarmTransitResult);
        }
        else {
            TextView tvAlarmNotice = (TextView) findViewById(R.id.tv_alarm_notice);
            tvAlarmNotice.setVisibility(View.INVISIBLE);

            TextView tvTitle = (TextView) findViewById(R.id.tv_title);
            tvTitle.setText(getString(R.string.tv_alarm_title_alarm_not_set));

            TextView tvRoute = (TextView) findViewById(R.id.tv_route);
            tvRoute.setVisibility(View.INVISIBLE);

            TextView tvAlarmAt = (TextView) findViewById(R.id.tv_alarm_at);
            tvAlarmAt.setVisibility(View.INVISIBLE);

            button.setVisibility(View.INVISIBLE);
        }

    }
    
    private SimpleTransitResult loadTransitResult() {
        long id = getTransitResultId();
        if (id < 0) {
            return null;
        }
        
        return new TransitResultDao(this).getTransitResult(id);
    }
    
    private long getTransitResultId() {
        return getIntent().getLongExtra(EXTRA_KEY_TRANSIT, -1);
    }
    
    /**
     * @see android.app.Activity#onDestroy()
     */
    @Override
    protected void onDestroy() {
        stopAlarm(false);
        super.onDestroy();
    }

    private void stopAlarm(boolean force) {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
        }
        
        if (vibrator != null) {
            vibrator.cancel();
        }
        
        boolean startAlarm = getIntent().getBooleanExtra(EXTRA_KEY_START_ALARM, false);
        if (startAlarm || force) {
            if (alarmTransitResult != null) {
                new TransitResultDao(this).updateAlarmStatus(getTransitResultId(), ALARM_STATUS_FINISHED);
            }
        }
    }
    
    private void startAlarm(SimpleTransitResult atr) {
        int textSize = Preferences.getTextSize(this);
        boolean startAlarm = getIntent().getBooleanExtra(EXTRA_KEY_START_ALARM, false);

        TextView tvTitle = (TextView) findViewById(R.id.tv_title);
        tvTitle.setTextSize(textSize);
        tvTitle.setText(ResultRenderer.createTitle(atr.getTransitResult()));

        TextView tvRoute = (TextView) findViewById(R.id.tv_route);
        tvRoute.setTextSize(textSize);
        tvRoute.setText(new TransitItem(atr.getTransitResult(), atr.getTransits().get(0)).toString());

        TextView tvAlarmNotice = (TextView) findViewById(R.id.tv_alarm_notice);
        tvAlarmNotice.setTextSize(textSize);
        tvAlarmNotice.setVisibility(startAlarm ? View.VISIBLE : View.INVISIBLE);

        TextView tvAlarmAt = (TextView) findViewById(R.id.tv_alarm_at);
        tvAlarmAt.setTextSize(textSize);
        tvAlarmAt.setText("アラーム： " + AlarmUtils.toDateTimeString(atr.getAlarmAt()));

        if (startAlarm) {
            if (atr.getAlarmStatus() != ALARM_STATUS_SET) {
                finish();
            }
            else {
                if (Preferences.isNoSoundButVibration(this)) {
                    vibrate();
                }
                else {
                    playAudio();
                }
            }
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
