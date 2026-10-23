package com.example.myappvosabessssss;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    private static final int CODIGO_PERMISOS = 100;

    // Componentes de la interfaz
    private Button btnAudio;
    private ImageView imgFoto;
    private VideoView vistaVideo;

    // Reproductor de audio (se crea al primer toque y se libera al salir)
    private MediaPlayer reproductor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // Respetar las barras del sistema (estado y navegación)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (vista, insets) -> {
            Insets barras = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            vista.setPadding(barras.left, barras.top, barras.right, barras.bottom);
            return insets;
        });

        solicitarPermisos();
        enlazarVistas();
        configurarAudio();
        configurarAnimacion();
        configurarVideo();
    }

    // Conecta los elementos del XML con las variables Java
    private void enlazarVistas() {
        btnAudio = findViewById(R.id.btnAudio);
        imgFoto = findViewById(R.id.imgFoto);
        vistaVideo = findViewById(R.id.vistaVideo);
    }

    // ---------- AUDIO ----------
    // El mismo botón sirve para reproducir y pausar
    private void configurarAudio() {
        btnAudio.setOnClickListener(v -> {
            if (reproductor == null) {
                reproductor = MediaPlayer.create(this, R.raw.mi_audio);
                if (reproductor == null) {
                    Toast.makeText(this, R.string.msg_audio_error, Toast.LENGTH_SHORT).show();
                    return;
                }
                // Cuando termina la canción, se restablece el botón
                reproductor.setOnCompletionListener(mp -> {
                    btnAudio.setText(R.string.btn_audio_play);
                    Toast.makeText(this, R.string.msg_audio_fin, Toast.LENGTH_SHORT).show();
                });
            }

            if (reproductor.isPlaying()) {
                reproductor.pause();
                btnAudio.setText(R.string.btn_audio_play);
            } else {
                reproductor.start();
                btnAudio.setText(R.string.btn_audio_pause);
            }
        });
    }

    // ---------- ANIMACIÓN ----------
    private void configurarAnimacion() {
        Button btnAnimar = findViewById(R.id.btnAnimar);
        btnAnimar.setOnClickListener(v -> {
            Animation efecto = AnimationUtils.loadAnimation(this, R.anim.aparecer_zoom);
            imgFoto.startAnimation(efecto);
        });
    }

    // ---------- VIDEO ----------
    private void configurarVideo() {
        // Barra de controles (play, pausa, avance) para el video
        MediaController controles = new MediaController(this);
        controles.setAnchorView(vistaVideo);
        vistaVideo.setMediaController(controles);

        Button btnVideo = findViewById(R.id.btnVideo);
        btnVideo.setOnClickListener(v -> {
            Uri ruta = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.mi_video);
            vistaVideo.setVideoURI(ruta);
            vistaVideo.start();
        });
    }

    // ---------- PERMISOS ----------
    private void solicitarPermisos() {
        boolean concedido = ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        if (!concedido) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, CODIGO_PERMISOS);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CODIGO_PERMISOS && grantResults.length > 0) {
            boolean ok = grantResults[0] == PackageManager.PERMISSION_GRANTED;
            Toast.makeText(this, ok ? R.string.msg_permiso_ok : R.string.msg_permiso_no,
                    Toast.LENGTH_SHORT).show();
        }
    }

    // ---------- CICLO DE VIDA ----------
    // Si la app pasa a segundo plano, se pausa el audio
    @Override
    protected void onPause() {
        super.onPause();
        if (reproductor != null && reproductor.isPlaying()) {
            reproductor.pause();
            btnAudio.setText(R.string.btn_audio_play);
        }
    }

    // Liberar recursos al cerrar la actividad
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (reproductor != null) {
            reproductor.release();
            reproductor = null;
        }
        vistaVideo.stopPlayback();
    }
}
