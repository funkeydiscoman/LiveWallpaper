package com.example.frankja.livewallpaper;

import android.graphics.Canvas;
import android.graphics.Movie;
import android.service.wallpaper.WallpaperService;
import android.util.Log;
import android.view.SurfaceHolder;



import java.io.IOException;
import android.os.Handler;

public class GIFWallpaperService extends WallpaperService {

    @Override
    public WallpaperService.Engine onCreateEngine() {

        Movie movie;
        try {
            movie = Movie.decodeStream(getResources().getAssets().open("daytime.gif"));
        }catch(IOException e){
            Log.d("GIF", "Could not load asset");
            return null;
        }
        return new GIFWallpaperEngine(movie);
    }

    private class GIFWallpaperEngine extends WallpaperService.Engine {

        private final int frameDuration = 20;
        private SurfaceHolder holder;
        private Movie movie;
        private boolean visible;
        private Handler handler;

        public GIFWallpaperEngine(Movie movie) {
            this.movie = movie;
            handler = new Handler();
        }

        @Override
        public void onCreate(SurfaceHolder surfaceHolder){
            super.onCreate(surfaceHolder);
            this.holder = surfaceHolder;
        }

        private Runnable drawGIF = new Runnable(){
            public void run(){
                draw();
            }
        };

        private void draw(){
            if(visible){
                Canvas canvas = holder.lockCanvas();
                canvas.save();
                canvas.scale(1f, 1f);
                movie.draw(canvas, 0, 0);
                canvas.restore();
                holder.unlockCanvasAndPost(canvas);
                movie.setTime((int) (System.currentTimeMillis() % movie.duration()));

                handler.removeCallbacks(drawGIF);
                handler.postDelayed(drawGIF, frameDuration);
            }
        }

        public void onVisibilityChanged(boolean visible){
            this.visible = visible;
            if(visible){
                handler.post(drawGIF);
            }
            else{
                handler.removeCallbacks(drawGIF);
            }
        }

        @Override
        public void onDestroy() {
            super.onDestroy();
            handler.removeCallbacks(drawGIF);
        }
    }
}
