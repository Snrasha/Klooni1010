package io.github.lonamiwebs.klooni;

import Intent.ACTION_SEND;
import android.content.Context;
import android.net.Uri;
import Intent.EXTRA_STREAM;
import android.content.Intent.EXTRA_SUBJECT;
import Intent.EXTRA_TEXT;
import java.io.File;
import android.widget.Toast;
import android.os.Handler;
import android.content.Intent;
import Toast.LENGTH_SHORT;

class AndroidShareChallenge extends ShareChallenge {
    private final Handler handler;

    private final Context context;

    AndroidShareChallenge(final Context context) {
        handler = new Handler();
        this.context = context;
    }

    @Override
    File getShareImageFilePath() {
        return new File(context.getExternalCacheDir(), "share_challenge.png");
    }

    @Override
    public void shareScreenshot(final boolean ok) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (!ok) {
                    Toast.makeText(context, "Failed to create the file", LENGTH_SHORT).show();
                    return ;
                }
                final String text = "Check out my score at 1010 Klooni!";
                final Uri pictureUri = Uri.fromFile(getShareImageFilePath());
                final Intent shareIntent = new Intent();
                shareIntent.setAction(ACTION_SEND);
                shareIntent.setType("image/png");
                shareIntent.putExtra(EXTRA_SUBJECT, "");
                shareIntent.putExtra(EXTRA_TEXT, text);
                shareIntent.putExtra(EXTRA_STREAM, pictureUri);
                context.startActivity(Intent.createChooser(shareIntent, "Challenge your friends..."));
            }
        });
    }
}

