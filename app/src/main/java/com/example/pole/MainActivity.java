package com.example.pole;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    Button game,admin,exit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        game = (Button) findViewById(R.id.buttonGame);
        admin = (Button) findViewById(R.id.buttonAdmin);
        exit = (Button) findViewById(R.id.buttonExit);
        //#### GAME ####//
        View.OnClickListener gameListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Новая игра");
                builder.setMessage("Введите количество игроков 1-6");
                final EditText input = new EditText(MainActivity.this);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT);
                input.setLayoutParams(lp);
                builder.setView(input);
                builder.setNegativeButton("Отмена",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int which) {
                                dialog.cancel();
                            }
                        });
                builder.setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int which) {
                                Intent intent = new Intent(MainActivity.this, GameActivity.class);
                                intent.putExtra("players", Integer.parseInt(input.getText().toString()));
                                startActivity(intent);
                            }
                        });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        };game.setOnClickListener(gameListener);
        //#### ADMIN ####//
        View.OnClickListener adminListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AdminActivity.class);
                startActivity(intent);
            }
        };admin.setOnClickListener(adminListener);
        //#### EXIT ####//
        View.OnClickListener exitListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        };exit.setOnClickListener(exitListener);
    }
}