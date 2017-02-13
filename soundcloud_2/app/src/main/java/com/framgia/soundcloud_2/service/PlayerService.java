package com.framgia.soundcloud_2.service;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.session.MediaSessionManager;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v7.app.NotificationCompat;

import com.framgia.soundcloud_2.R;
import com.framgia.soundcloud_2.data.model.Track;
import com.framgia.soundcloud_2.main.MainActivity;
import com.framgia.soundcloud_2.utils.DatabaseManager;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Random;

import static com.framgia.soundcloud_2.utils.Constant.ConstantApi.EXTRA_IMAGE_URL;
import static com.framgia.soundcloud_2.utils.Constant.ConstantApi.EXTRA_TITLE;
import static com.framgia.soundcloud_2.utils.Constant.ConstantApi.EXTRA_USER_NAME;
import static com.framgia.soundcloud_2.utils.Constant.KeyIntent.ACTION_BACKWARD;
import static com.framgia.soundcloud_2.utils.Constant.KeyIntent.ACTION_FORWARD;
import static com.framgia.soundcloud_2.utils.Constant.KeyIntent.ACTION_GET_AUDIO_STATE;
import static com.framgia.soundcloud_2.utils.Constant.KeyIntent.ACTION_NO_REPEAT;
import static com.framgia.soundcloud_2.utils.Constant.KeyIntent.ACTION_NO_SHUFFLE;
import static com.framgia.soundcloud_2.utils.Constant.KeyIntent.ACTION_PLAY_NEW_SONG;
import static com.framgia.soundcloud_2.utils.Constant.KeyIntent.ACTION_REPEAT;
import static com.framgia.soundcloud_2.utils.Constant.KeyIntent.ACTION_SEEK_TO;
import static com.framgia.soundcloud_2.utils.Constant.KeyIntent.ACTION_SHUFFLE;
import static com.framgia.soundcloud_2.utils.Constant.KeyIntent.ACTION_UPDATE_CONTROL;
import static com.framgia.soundcloud_2.utils.Constant.KeyIntent.ACTION_NEXT;
import static com.framgia.soundcloud_2.utils.Constant.KeyIntent.ACTION_PAUSE;
import static com.framgia.soundcloud_2.utils.Constant.KeyIntent.ACTION_PLAY;
import static com.framgia.soundcloud_2.utils.Constant.KeyIntent.ACTION_PREVIOUS;
import static com.framgia.soundcloud_2.utils.Constant.KeyIntent.ACTION_STOP;
import static com.framgia.soundcloud_2.utils.Constant.KeyIntent.ACTION_UPDATE_CONTROL_DURATION;
import static com.framgia.soundcloud_2.utils.Constant.KeyIntent.ACTION_UPDATE_SEEK_BAR;
import static com.framgia.soundcloud_2.utils.Constant.KeyIntent.ACTION_UPDATE_AUDIO;
import static com.framgia.soundcloud_2.utils.Constant.KeyIntent.ACTION_UPDATE_AUDIO_DURATION;
import static com.framgia.soundcloud_2.utils.Constant.KeyIntent.EXTRA_DURATION;
import static com.framgia.soundcloud_2.utils.Constant.KeyIntent.EXTRA_FULL_DURATION;
import static com.framgia.soundcloud_2.utils.Constant.KeyIntent.EXTRA_ICON_BACKWARD;
import static com.framgia.soundcloud_2.utils.Constant.KeyIntent.EXTRA_ICON_FORWARD;
import static com.framgia.soundcloud_2.utils.Constant.KeyIntent.EXTRA_ICON_PLAY_PAUSE;
import static com.framgia.soundcloud_2.utils.Constant.KeyIntent.EXTRA_ICON_REPEAT_NOREPEAT;
import static com.framgia.soundcloud_2.utils.Constant.KeyIntent.EXTRA_ICON_SHUFFLE_NOSHUFFLE;
import static com.framgia.soundcloud_2.utils.StorePreferences.loadAudioIndex;
import static com.framgia.soundcloud_2.utils.StorePreferences.storeAudioIndex;
;

/**
 * Created by Vinh on 04/02/2017.
 */
public class PlayerService extends Service implements MediaPlayer.OnCompletionListener,
    MediaPlayer.OnPreparedListener {
    public static final String SONG_PLAYER = "SONG_PLAYER";
    private static final int NOTIFICATION_ID = 101;
    private final int SEEKBAR_DELAY_TIME = 1000;
    private Handler mSeekBarHandler = new Handler();
    private MediaPlayer mMediaPlayer;
    private MediaSessionManager mMediaSessionManager;
    private MediaSessionCompat mMediaSession;
    private MediaControllerCompat.TransportControls mTransportControls;
    private int mResumePosition;
    private List<Track> mListTrack;
    private int mAudioIndex = -1;
    private Track mTrack;
    private DatabaseManager mDatabaseHelper;
    private Bitmap mIconBitmap;
    private boolean isShuffle;
    private boolean isRepeat;
    private int seekForwardTime = 10000;
    private int seekBackwardTime = 10000;

    public enum SongStatus {
        PLAYING, PAUSED;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        try {
            mDatabaseHelper = new DatabaseManager(getApplicationContext());
            loadSong();
        } catch (NullPointerException e) {
            stopSelf();
        }
        if (mMediaSessionManager == null) {
            try {
                initMediaSession();
                initMediaPlayer();
            } catch (RemoteException e) {
                e.printStackTrace();
                stopSelf();
            }
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        catchPlayerEvent(intent);
        catchPlayerEventDuration(intent);
        return super.onStartCommand(intent, flags, startId);
    }

    private Runnable mUpdateTimeTask = new Runnable() {
        public void run() {
            try {
                if (mMediaPlayer.isPlaying()) {
                    Intent broadcastIntent = new Intent(ACTION_UPDATE_CONTROL_DURATION);
                    broadcastIntent.setAction(ACTION_UPDATE_SEEK_BAR);
                    broadcastIntent.putExtra(EXTRA_FULL_DURATION, mMediaPlayer.getDuration());
                    broadcastIntent.putExtra(EXTRA_DURATION, mMediaPlayer.getCurrentPosition());
                    getApplicationContext().sendBroadcast(broadcastIntent);
                }
                mSeekBarHandler.postDelayed(this, SEEKBAR_DELAY_TIME);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    public void updateSeekBar() {
        try {
            mSeekBarHandler.postDelayed(mUpdateTimeTask, SEEKBAR_DELAY_TIME);
        } catch (Exception e) {
        }
    }

    private void catchPlayerEvent(Intent playerEvent) {
        if (playerEvent == null || playerEvent.getAction() == null) return;
        String actionString = playerEvent.getAction();
        Intent broadcastIntent = new Intent(ACTION_UPDATE_CONTROL);
        broadcastIntent.setAction(actionString);
        sendBroadcast(broadcastIntent);
        switch (actionString) {
            case ACTION_PLAY:
                mTransportControls.play();
                break;
            case ACTION_PAUSE:
                mTransportControls.pause();
                break;
            case ACTION_NEXT:
                mTransportControls.skipToNext();
                break;
            case ACTION_PREVIOUS:
                mTransportControls.skipToPrevious();
                break;
            case ACTION_STOP:
                mTransportControls.stop();
                break;
            case ACTION_PLAY_NEW_SONG:
                playSelectedSong();
                break;
            case ACTION_BACKWARD:
                backWard();
                break;
            case ACTION_FORWARD:
                forWard();
                break;
            case ACTION_REPEAT:
                repeatSong();
                break;
            case ACTION_NO_REPEAT:
                noRepeatSong();
                break;
            case ACTION_SHUFFLE:
                shuffleSong();
                break;
            case ACTION_NO_SHUFFLE:
                noShuffleSong();
                break;
            case ACTION_GET_AUDIO_STATE:
                sendBroadcastUpdate();
                break;
            default:
                break;
        }
    }

    private void catchPlayerEventDuration(Intent playerEvent) {
        if (playerEvent == null || playerEvent.getAction() == null) return;
        String actionString = playerEvent.getAction();
        Intent broadcastIntent = new Intent(ACTION_UPDATE_CONTROL_DURATION);
        broadcastIntent.setAction(actionString);
        sendBroadcast(broadcastIntent);
        switch (actionString) {
            case ACTION_GET_AUDIO_STATE:
                sendBroadcastUpdateDuration();
                break;
            case ACTION_SEEK_TO:
                int duration = playerEvent.getExtras().getInt(EXTRA_DURATION);
                seekTo(duration);
            default:
                break;
        }
    }

    public void loadSong() {
        mListTrack = mDatabaseHelper.getListTrack();
        mAudioIndex = loadAudioIndex(getApplicationContext());
        if (mAudioIndex == -1 || mAudioIndex > mListTrack.size())
            mAudioIndex = 0;
        mTrack = mListTrack.get(mAudioIndex);
    }

    public void initMediaPlayer() {
        if (mMediaPlayer == null) mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setOnCompletionListener(this);
        mMediaPlayer.setOnPreparedListener(this);
        mMediaPlayer.reset();
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            mMediaPlayer.setDataSource(mTrack.getUri());
        } catch (IOException e) {
            e.printStackTrace();
            stopSelf();
        }
        mMediaPlayer.prepareAsync();
    }

    public void playMedia() {
        if (mMediaPlayer.isPlaying()) return;
        mMediaPlayer.start();
        updateSeekBar();
    }

    public void playSelectedSong() {
        loadSong();
        if (mMediaPlayer.isPlaying()) {
            stopMedia();
            mMediaPlayer.reset();
        }
        sendBroadcastUpdate();
        initMediaPlayer();
        buildNotification(SongStatus.PLAYING);
    }

    public void backWard() {
        mResumePosition = mMediaPlayer.getCurrentPosition();
        if (mResumePosition - seekBackwardTime >= 0) {
            mMediaPlayer.seekTo(mResumePosition - seekBackwardTime);
        } else {
            mMediaPlayer.seekTo(0);
        }
    }

    public void forWard() {
        mResumePosition = mMediaPlayer.getCurrentPosition();
        if (mResumePosition + seekForwardTime <= mMediaPlayer.getDuration()) {
            mMediaPlayer.seekTo(mResumePosition + seekForwardTime);
        } else {
            mMediaPlayer.seekTo(mMediaPlayer.getDuration());
        }
    }

    public void stopMedia() {
        if (mMediaPlayer == null || !mMediaPlayer.isPlaying()) return;
        mMediaPlayer.stop();
    }

    public void pauseMedia() {
        if (!mMediaPlayer.isPlaying()) return;
        mMediaPlayer.pause();
        mResumePosition = mMediaPlayer.getCurrentPosition();
        sendBroadcastUpdate();
    }

    public void resumeMedia() {
        if (mMediaPlayer.isPlaying()) return;
        mMediaPlayer.seekTo(mResumePosition);
        mMediaPlayer.start();
        sendBroadcastUpdate();
    }

    public void skipToNext() {
        if (mAudioIndex == mListTrack.size() - 1) mAudioIndex = -1;
        mTrack = mListTrack.get(++mAudioIndex);
        stopMedia();
        mMediaPlayer.reset();
        initMediaPlayer();
        sendBroadcastUpdate();
    }

    public void skipToPrevious() {
        if (mAudioIndex == 0) mAudioIndex = mListTrack.size();
        mTrack = mListTrack.get(--mAudioIndex);
        stopMedia();
        mMediaPlayer.reset();
        initMediaPlayer();
        sendBroadcastUpdate();
    }

    public void repeatSong() {
        isRepeat = true;
        isShuffle = false;
        sendBroadcastUpdate();
    }

    public void noRepeatSong() {
        isRepeat = false;
        sendBroadcastUpdate();
    }

    public void shuffleSong() {
        isShuffle = true;
        isRepeat = false;
        sendBroadcastUpdate();
    }

    public void seekTo(int position) {
        mMediaPlayer.seekTo(position);
        if (mMediaPlayer.isPlaying()) return;
        mMediaPlayer.start();
        updateSeekBar();
    }

    public void noShuffleSong() {
        isShuffle = false;
    }

    private void initMediaSession() throws RemoteException {
        if (mMediaSessionManager != null) return;
        mMediaSessionManager =
            (MediaSessionManager) getSystemService(Context.MEDIA_SESSION_SERVICE);
        mMediaSession = new MediaSessionCompat(getApplicationContext(), SONG_PLAYER);
        mTransportControls = mMediaSession.getController().getTransportControls();
        mMediaSession.setActive(true);
        mMediaSession.setFlags(MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);
        mMediaSession.setCallback(new MediaSessionCompat.Callback() {
            @Override
            public void onPlay() {
                super.onPlay();
                resumeMedia();
            }

            @Override
            public void onPause() {
                super.onPause();
                pauseMedia();
            }

            @Override
            public void onSkipToNext() {
                super.onSkipToNext();
                skipToNext();
            }

            @Override
            public void onSkipToPrevious() {
                super.onSkipToPrevious();
                skipToPrevious();
            }

            @Override
            public void onStop() {
                super.onStop();
                stopForeground(true);
                pauseMedia();
            }

            @Override
            public void onSeekTo(long position) {
                super.onSeekTo(position);
            }
        });
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        playMedia();
        sendBroadcastUpdate();
        sendBroadcastUpdateDuration();
        buildNotification(SongStatus.PLAYING);
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        if (isRepeat) {
            isRepeat = true;
            mTrack = mListTrack.get(mAudioIndex);
            storeAudioIndex(getApplicationContext(), mAudioIndex);
            stopMedia();
            mMediaPlayer.reset();
            sendBroadcastUpdate();
            initMediaPlayer();
        } else if (isShuffle) {
            isShuffle = true;
            Random rand = new Random();
            mAudioIndex = rand.nextInt((mListTrack.size()));
            mTrack = mListTrack.get(mAudioIndex);
            storeAudioIndex(getApplicationContext(), mAudioIndex);
            stopMedia();
            mMediaPlayer.reset();
            sendBroadcastUpdate();
            initMediaPlayer();
        } else {
            isRepeat = false;
            isShuffle = false;
            skipToNext();
            buildNotification(SongStatus.PLAYING);
        }
    }

    public void buildNotification(SongStatus songStatus) {
        int notificationAction = android.R.drawable.ic_media_pause;
        PendingIntent playPauseAction = null;
        if (songStatus == SongStatus.PLAYING) {
            notificationAction = android.R.drawable.ic_media_pause;
            playPauseAction = playbackAction(ACTION_PAUSE);
        } else if (songStatus == SongStatus.PAUSED) {
            notificationAction = android.R.drawable.ic_media_play;
            playPauseAction = playbackAction(ACTION_PLAY);
        }
        if (mIconBitmap == null) {
            mIconBitmap = BitmapFactory.decodeResource(getResources(),
                R.drawable.ic_songs);
            new GetBitmapImage().execute(mTrack.getArtworkUrl());
        }
        NotificationCompat.Builder notificationBuilder =
            (NotificationCompat.Builder) new NotificationCompat.Builder(this)
                .setShowWhen(false)
                .setStyle(new NotificationCompat.MediaStyle()
                    .setMediaSession(mMediaSession.getSessionToken())).setColor(getResources()
                    .getColor(R.color.color_purple))
                .setLargeIcon(mIconBitmap)
                .setSmallIcon(android.R.drawable.stat_sys_headset)
                .setContentText(mTrack.getUser().getUserName())
                .setContentTitle(mTrack.getTitle())
                .addAction(android.R.drawable.ic_media_previous, ACTION_PREVIOUS,
                    playbackAction(ACTION_PREVIOUS))
                .addAction(android.R.drawable.ic_media_rew, ACTION_BACKWARD, playbackAction
                    (ACTION_BACKWARD))
                .addAction(notificationAction, ACTION_PLAY, playPauseAction)
                .addAction(android.R.drawable.ic_media_next, ACTION_NEXT,
                    playbackAction(ACTION_NEXT))
                .addAction(android.R.drawable.ic_menu_close_clear_cancel, ACTION_STOP,
                    playbackAction(ACTION_STOP));
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
            new Intent(this, MainActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);
        notificationBuilder.setContentIntent(contentIntent);
        startForeground(NOTIFICATION_ID, notificationBuilder.build());
    }

    public void removeNotification() {
        NotificationManager notificationManager =
            (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(NOTIFICATION_ID);
    }

    private PendingIntent playbackAction(String actionNumber) {
        Intent playbackAction = new Intent(this, PlayerService.class);
        switch (actionNumber) {
            case ACTION_PLAY:
                playbackAction.setAction(ACTION_PLAY);
                return PendingIntent.getService(this, 0, playbackAction, 0);
            case ACTION_PAUSE:
                playbackAction.setAction(ACTION_PAUSE);
                return PendingIntent.getService(this, 0, playbackAction, 0);
            case ACTION_NEXT:
                playbackAction.setAction(ACTION_NEXT);
                return PendingIntent.getService(this, 0, playbackAction, 0);
            case ACTION_PREVIOUS:
                playbackAction.setAction(ACTION_PREVIOUS);
                return PendingIntent.getService(this, 0, playbackAction, 0);
            case ACTION_STOP:
                playbackAction.setAction(ACTION_STOP);
                return PendingIntent.getService(this, 0, playbackAction, 0);
            case ACTION_BACKWARD:
                playbackAction.setAction(ACTION_BACKWARD);
                return PendingIntent.getService(this, 0, playbackAction, 0);
            case ACTION_FORWARD:
                playbackAction.setAction(ACTION_FORWARD);
                return PendingIntent.getService(this, 0, playbackAction, 0);
            default:
                break;
        }
        return null;
    }

    private void sendBroadcastUpdate() {
        Intent broadcastIntent = new Intent(ACTION_UPDATE_CONTROL);
        broadcastIntent.setAction(ACTION_UPDATE_AUDIO);
        broadcastIntent.putExtra(EXTRA_TITLE, mTrack.getTitle());
        broadcastIntent.putExtra(EXTRA_USER_NAME, mTrack.getUser().getUserName());
        broadcastIntent.putExtra(EXTRA_IMAGE_URL, mTrack.getArtworkUrl());
        broadcastIntent.putExtra(EXTRA_ICON_PLAY_PAUSE, mMediaPlayer.isPlaying());
        broadcastIntent.putExtra(EXTRA_ICON_REPEAT_NOREPEAT, isRepeat);
        broadcastIntent.putExtra(EXTRA_ICON_SHUFFLE_NOSHUFFLE, isShuffle);
        broadcastIntent.putExtra(EXTRA_ICON_FORWARD, mMediaPlayer.getCurrentPosition());
        broadcastIntent.putExtra(EXTRA_ICON_BACKWARD, mMediaPlayer.getCurrentPosition());
        getApplicationContext().sendBroadcast(broadcastIntent);
    }

    private void sendBroadcastUpdateDuration() {
        Intent broadcastIntent = new Intent(ACTION_UPDATE_CONTROL_DURATION);
        broadcastIntent.setAction(ACTION_UPDATE_AUDIO_DURATION);
        broadcastIntent.putExtra(EXTRA_DURATION, mMediaPlayer.getCurrentPosition());
        broadcastIntent.putExtra(EXTRA_FULL_DURATION, mMediaPlayer.getDuration());
        getApplicationContext().sendBroadcast(broadcastIntent);
    }

    private Bitmap getBitmapFromURL(String strUrl) {
        if (strUrl == null) return BitmapFactory.decodeResource(getResources(),
            R.drawable.ic_songs);
        try {
            URL url = new URL(strUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            return BitmapFactory.decodeStream(input);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public class GetBitmapImage extends AsyncTask<String, Void, Bitmap> {
        @Override
        protected Bitmap doInBackground(String... strings) {
            return getBitmapFromURL(strings[0]);
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            if (bitmap == null) return;
            mIconBitmap = bitmap;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mMediaPlayer != null) {
            stopMedia();
            mMediaPlayer.release();
        }
        removeNotification();
        mDatabaseHelper.clearListTrack();
    }
}
