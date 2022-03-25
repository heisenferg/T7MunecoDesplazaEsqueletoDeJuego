package com.example.esqueletodejuego;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Log;
import android.view.Display;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.annotation.NonNull;

public class Juego extends SurfaceView implements SurfaceHolder.Callback {
    private Bitmap bmpMapa;
    private Bitmap mario;
    private SurfaceHolder holder;
    private BucleJuego bucle;

    private int x=0,y=0; //Coordenadas x e y para desplazar

    private static final int bmpInicialx=500;
    private static final int bmpInicialy=500;
    private static final int rectInicialx=450;
    private static final int rectInicialy=450;
    private static final int arcoInicialx=50;
    private static final int arcoInicialy=20;
    private static final int textoInicialx=50;
    private static final int textoInicialy=20;


    private int maxX=0;
    private int maxY=0;
    private int contadorFrames=0;
    private boolean hacia_abajo=true;
    private static final String TAG = Juego.class.getSimpleName();
    private int xMario=0, yMario=0;
    private int mapaH, mapaW;
    private int destMapaY;
    private int estado_mario=0;
    private int puntero_mario_sprite =0;
    private int marioW, marioH;
    private int contador_Frames = 0;





    public Juego(Activity context) {
        super(context);
        holder = getHolder();
        holder.addCallback(this);
        Display mdisp = context.getWindowManager().getDefaultDisplay();
        bmpMapa = BitmapFactory.decodeResource(getResources(), R.drawable.mapamario);
        mario = BitmapFactory.decodeResource(getResources(), R.drawable.mario);

        mapaH = bmpMapa.getHeight();
        mapaW = bmpMapa.getWidth();


       Point mdispSize = new Point();
        mdisp.getSize(mdispSize);
        maxX = mdispSize.x;
        maxY = mdispSize.y;
    }


    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        // se crea la superficie, creamos el game loop

        // Para interceptar los eventos de la SurfaceView
        getHolder().addCallback(this);

        // creamos el game loop
        bucle = new BucleJuego(getHolder(), this);

        // Hacer la Vista focusable para que pueda capturar eventos
        setFocusable(true);

        //comenzar el bucle
        bucle.start();

    }

    /**
     * Este método actualiza el estado del juego. Contiene la lógica del videojuego
     * generando los nuevos estados y dejando listo el sistema para un repintado.
     */
    public void actualizar() {
        xMario = xMario+1000/(bucle.MAX_FPS*3);

        marioW = mario.getWidth();
        marioH = mario.getHeight();

        contadorFrames++;
        destMapaY = (maxY-mapaH)/2;
        //Posición marioY
        yMario = destMapaY+mapaH*9/10-mario.getHeight()*2/3;
        puntero_mario_sprite = marioW/21*estado_mario;
        contadorFrames++;

        if (contadorFrames%3==0){
            estado_mario++;

            if (estado_mario>3){
                estado_mario=0;
            }
        }


    }

    /**
     * Este método dibuja el siguiente paso de la animación correspondiente
     */

    public void renderizar(Canvas canvas) {
        if(canvas!=null) {

            Paint myPaint = new Paint();
            myPaint.setStyle(Paint.Style.STROKE);

            //Toda el canvas en rojo
            canvas.drawColor(Color.RED);



            //Dibujar mapa
            canvas.drawBitmap(bmpMapa, 0, destMapaY, null);

            //Dibujar muñeco
            //canvas.drawBitmap(mario, xMario, yMario, null);
            //Recortar muñeco
            canvas.drawBitmap(mario, new Rect(puntero_mario_sprite,0,puntero_mario_sprite+marioW/21, marioH*2/3),
                    new Rect(100, yMario, 100+marioW/21, destMapaY+mapaH*9/10), null);

            //dibujar un texto
            myPaint.setStyle(Paint.Style.FILL);
            myPaint.setTextSize(40);
            canvas.drawText("Frames ejecutados:"+contadorFrames, 600, 1000, myPaint);

        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.d(TAG, "Juego destruido!");
        // cerrar el thread y esperar que acabe
        boolean retry = true;
        while (retry) {
            try {
               // bucle.fin();
                bucle.join();
                retry = false;
            } catch (InterruptedException e) {

            }
        }
    }





}
