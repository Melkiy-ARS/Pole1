package com.example.pole;

import android.content.DialogInterface;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class GameActivity extends AppCompatActivity {

    public Handler handler, handlerWave, handlerEnd;
    String[] players;
    int[] points;
    static int addPoint;
    static char known[];
    static boolean wait = false;
    Word gameWord;
    public int angle = 0,wave = 0;
    ArrayList<Word> wordList;
    static ArrayList<Button> buttons;
    boolean canCircle = false, canLetter = false;
    private final static String FILE_NAME = "database.txt";
    final String DEF_NAME = "Игрок";
    TextView wordView;
    ImageView circle;
    LinearLayout container;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        wordView = (TextView) findViewById(R.id.wordView);
        circle = (ImageView) findViewById(R.id.imageView);
        container = (LinearLayout) findViewById(R.id.linearLayout);

        int loop = getIntent().getIntExtra("players", 0);
        if(loop<1)loop = 1;else if(loop>6)loop=6;
        players = new String[loop];
        points = new int[loop];
        wordList = new ArrayList<Word>();
        //Вводим имена игроков
        wordView.setTextSize(20f);
        Log.d("Players: ",loop + "");
        for(int i = loop; i > 0; i--){
            AlertDialog.Builder builder = new AlertDialog.Builder(GameActivity.this);
            builder.setTitle("Игрок " + i);
            builder.setMessage("Введите имя игрока");
            final EditText input = new EditText(GameActivity.this);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT);
            input.setLayoutParams(lp);
            builder.setView(input);
            int finalI = i-1;
            builder.setPositiveButton("OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,int which) {
                            if(input.getText().length()>0){
                                players[finalI] = input.getText().toString();
                            }else{
                                players[finalI] = DEF_NAME + " " + (finalI + 1);
                            }
                        }
                    });
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }//Конец записи имен игроков
        load();
        Random rand = new Random();
        gameWord = wordList.get(rand.nextInt()%wordList.size());//Выбор слова
        buttons = new ArrayList<Button>();

        wordView.setText(gameWord.getDescription());

        known = new char[gameWord.getWord().length()];
        for(int i = 0; i < known.length; i++){known[i]=' ';}

        Display display = getWindowManager(). getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int x = size.x/12;

        int finalLoop1 = loop;
        handlerEnd = new Handler() {
            @Override
            public void handleMessage(android.os.Message msg) {
                String res = "";
                for(int i = 0; i<finalLoop1; i++){
                    res+=players[i] + " " + points[i] + "\n";
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(GameActivity.this);
                builder.setTitle("Конец игры");
                builder.setMessage(res);
                builder.setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int which) {
                               finish();
                            }
                        });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        };
        handlerWave = new Handler() {
            @Override
            public void handleMessage(android.os.Message msg) {
                Toast.makeText(getBaseContext(), "Ходит: "+ players[wave % finalLoop1], Toast.LENGTH_LONG).show();
            }
        };

        handler = new Handler() {
            @Override
            public void handleMessage(android.os.Message msg) {
                AlertDialog.Builder builder = new AlertDialog.Builder(GameActivity.this);
                builder.setTitle(players[wave % finalLoop1]);
                builder.setMessage("Введите букву");
                final EditText input = new EditText(GameActivity.this);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT);
                input.setLayoutParams(lp);
                builder.setView(input);
                int finalPoint = addPoint;
                builder.setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int which) {
                                boolean p = false;
                                char in = input.getText().toString().toLowerCase().replace(" ", "").charAt(0);
                                for(int i = 0; i < gameWord.getWord().length(); i++){
                                    if(in == gameWord.getWord().toLowerCase().charAt(i)){
                                        known[i]=in;
                                        p = true;
                                        buttons.get(i).setText(""+in);
                                    }
                                }
                                if(p){points[wave % finalLoop1] = points[wave % finalLoop1] + finalPoint;
                                wave--;}
                                wait = false;
                            }
                        });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        };

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(canLetter) {
                    int work = view.getId();
                    work -= 2000;
                    char t = gameWord.getWord().charAt(work);
                    buttons.get(work).setText(""+t);
                    known[work] = t;
                    canLetter = false;
                }
            }
        };

        for(int i = 0; i < gameWord.getWord().length(); i++){
            Button button = new Button(this);
            button.setId(2000+i);
            button.setLayoutParams (new ViewGroup.LayoutParams(x, ViewGroup.LayoutParams.WRAP_CONTENT));
            button.setOnClickListener(listener);
            buttons.add(button);
            container.addView(button);
        }
        //Поток для ходов
        int finalLoop = loop;
        new Thread(new Runnable() {
            public void run() {
                while (true) {
                    handlerWave.sendEmptyMessage(0);
                    canCircle = true;
                    while (canCircle) {
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    } //Ожидание барабана
                    Log.d("Angle", ""+angle);
                    int point = 0;
                    angle = angle%360;
                    if (angle > 315) {
                        point = 25;
                    } else if (angle > 270) {
                        point = points[wave % finalLoop]; //Можна было сделать Банкрот но круг слишком маленький и шанс слишком большой
                    } else if (angle > 225) {
                        point = 50;
                    } else if (angle > 180) {
                        point = 150; //Нету призов, пока не придумал чем награждать
                    } else if (angle > 135) {
                        point = 10;
                    } else if (angle > 90) {
                        point = 0;
                    } else if (angle > 45) {
                        point = 100;
                    } else {
                        point = 50;
                        canLetter = true;
                        points[wave % finalLoop] = points[wave % finalLoop] + 25;
                    }
                    Log.d("Point", ""+point);

                    addPoint = point;
                    wait = true;
                    Log.d("start of waiting", "");
                    handler.sendEmptyMessage(0);
                    while (wait) {
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    } //Ожидание ввода
                    Log.d("End of waiting", "");
                    wave++;
                    //проверим всё ли узнали
                    int count = 0;
                    for(int i = 0; i < known.length; i++){
                        if(known[i]!=' '){
                            count++;
                        }
                    }
                    if(count==gameWord.getWord().length()){
                        //Игра окончена, подводим итоги
                        handlerEnd.sendEmptyMessage(0);
                    }
                }
            }
        }).start();

        View.OnClickListener circleListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int rotate = rand.nextInt(1800);
                angle = (angle+rotate)%360;
                Log.d("rotate", rotate+"");
                //Поток для красивой прокрутки;
                new Thread(new Runnable() {
                    public void run() {
                        int moving = rotate;
                        while (moving > 0) {
                            if (moving > 1000) {
                                moving -= 10;
                                circle.setRotation(circle.getRotation() + (float) 10);
                            } else if (moving > 500) {
                                moving -= 5;
                                circle.setRotation(circle.getRotation() + (float) 5);
                            } else if (moving > 100) {
                                moving -= 2;
                                circle.setRotation(circle.getRotation() + (float) 2);
                            } else  {
                                moving -= 1;
                                circle.setRotation(circle.getRotation() + (float) 1);
                            }

                            try {
                                Thread.sleep(15);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                        canCircle = false;
                    }
                }).start();
            }
        };circle.setOnClickListener(circleListener);

    }

    public void load(){ //Метод для полученния данных из файла и загрузки в wordList
        String text = "";
        FileInputStream fin = null;
        try {
            fin = openFileInput(FILE_NAME);
            byte[] bytes = new byte[fin.available()];
            fin.read(bytes);
            text = new String (bytes);
            Log.d("Load of data",text);
            wordList.clear();
            if(text.length()!=0) {
                String[] loops = text.split("&");
                for (int i = 0; i < loops.length; i++) {
                    Word word = new Word(loops[i]);
                    wordList.add(word);
                }
            }
        }
        catch(IOException ex) {
            Toast.makeText(getBaseContext(), ex.getMessage(), Toast.LENGTH_SHORT).show();
        }
        finally{
            try{
                if(fin!=null)
                    fin.close();
            }
            catch(IOException ex){
                Toast.makeText(getBaseContext(), ex.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

}
