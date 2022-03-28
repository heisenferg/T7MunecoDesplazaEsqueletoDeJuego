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
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class Juego extends SurfaceView implements SurfaceHolder.Callback, View.OnTouchListener {
    private Bitmap bmpMapa;
    private Bitmap mario;
    private SurfaceHolder holder;
    private BucleJuego bucle;

    private int x=0,y=1; //Coordenadas x e y para desplazar

    private int maxX=0;
    private int maxY=0;
    private int contadorFrames=0;
    private boolean hacia_abajo=true;
    private static final String TAG = Juego.class.getSimpleName();
   // private int xMario=0, yMario=0;
    private int mapaH, mapaW;
    private int destMapaY;
    private int estado_mario=0;
    private int puntero_mario_sprite =0;
    private int marioW, marioH;
    private int contador_Frames = 0;
private int yMario;
    private float posicionMario[] = new float[2];
    private float velocidadMario [] = new float[2];

    private float gravedad [] =new float[2];
    private float posicionInicialMario[]= new float[2];

    private int tiempoCrucePantalla = 3;
    private float deltaT;
    private boolean salta = false;




    public Juego(Activity context) {
        super(context);
        holder = getHolder();
        holder.addCallback(this);
        Display mdisp = context.getWindowManager().getDefaultDisplay();
        bmpMapa = BitmapFactory.decodeResource(getResources(), R.drawable.mapamario);
        mario = BitmapFactory.decodeResource(getResources(), R.drawable.mario);

        mapaH = bmpMapa.getHeight();
        mapaW = bmpMapa.getWidth();

        deltaT = 1f/BucleJuego.MAX_FPS;


       Point mdispSize = new Point();
        mdisp.getSize(mdispSize);


        velocidadMario[x] = maxX/tiempoCrucePantalla;
        velocidadMario[y] = 0;

    }


    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {

        // Para interceptar los eventos de la SurfaceView
        getHolder().addCallback(this);
        Canvas c = holder.lockCanvas();
        maxX =c.getWidth();
        maxY = c.getHeight();
        holder.unlockCanvasAndPost(c);

        marioW = mario.getWidth();
        marioH = mario.getHeight();
        // creamos el game loop


        posicionInicialMario[x] = maxX*0.1f;

        destMapaY = (maxY-mapaH)/2;

      //  posicionMario[y] = destMapaY + marioH * 9/10;

        posicionInicialMario[y]= destMapaY + (mapaH*9/10)-(marioH*2/3);

        posicionMario[x]=posicionInicialMario[x];
        posicionMario[y]=posicionInicialMario[y];

        velocidadMario[x]= maxX/tiempoCrucePantalla;
        velocidadMario[y]= -velocidadMario[x]*2;

        gravedad[x] = 0f;
        gravedad[y] = -velocidadMario[y]*2;

        // se crea la superficie, creamos el game loop
        bucle = new BucleJuego(getHolder(), this);
        setOnTouchListener(this);

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

        //Vector de velocidad
       // xMario = xMario+mapaW/(bucle.MAX_FPS*3);


        contadorFrames++;
        //Posición marioY
        yMario = destMapaY+mapaH*9/10-mario.getHeight()*2/3;
        puntero_mario_sprite = marioW/21*estado_mario;
        contadorFrames++;



        //Velocidad
        posicionMario[x] = posicionMario[x] + deltaT * velocidadMario[x];
        posicionMario[y] = posicionMario[y] + deltaT * velocidadMario[y];

//Gravedad
        velocidadMario[x] = velocidadMario[x] + deltaT*gravedad[x];
        velocidadMario[y] = velocidadMario[y] + deltaT*gravedad[y];

        estado_mario++;

        if (contadorFrames%3==0){

            if (estado_mario>3){
                estado_mario=0;
            }
        }

        if (posicionMario[x] > maxX+(marioW/21) || posicionMario[x]<=0) {
            velocidadMario[x] = velocidadMario[x] * -1;
        }

        //Rebote
        if(posicionMario[y]>=posicionInicialMario[y]){
            velocidadMario[y]= -(maxX/tiempoCrucePantalla)*2;
            posicionMario[y] = posicionInicialMario[y];

        }

        if(salta){
           // bucle.ejecutandose=false;
            velocidadMario[y]=-velocidadMario[x]*2;
            gravedad[y]=-velocidadMario[y]*2;
            salta = false;
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
                    new Rect( (int)posicionMario[x], yMario, (int) posicionMario[x]+marioW/21, destMapaY+mapaH*9/10), null);

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


    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getActionMasked()){
            case MotionEvent.ACTION_UP:
                salta = true;

        }
        return true;
    }
}
