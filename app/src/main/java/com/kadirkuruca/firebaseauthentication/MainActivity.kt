package com.kadirkuruca.firebaseauthentication

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    lateinit var mAuthStateListener : FirebaseAuth.AuthStateListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initAuthStateListener()
    }

    private fun setKullaniciBilgileri(){
        var kullanici = FirebaseAuth.getInstance().currentUser
        if(kullanici != null){
            if(kullanici.displayName.isNullOrEmpty()){
                tvKullaniciAdi.setText("Tanımlanmadı")
            }else tvKullaniciAdi.setText(kullanici.displayName)

            tvKullaniciEmail.setText(kullanici.email)
            tvKullaniciUID.setText(kullanici.uid)
        }
    }

    private fun initAuthStateListener() {
        mAuthStateListener = object : FirebaseAuth.AuthStateListener{
            override fun onAuthStateChanged(p0: FirebaseAuth) {
                var kullanici = p0.currentUser
                if(kullanici != null){

                }else{
                    var cikisIntent = Intent(this@MainActivity,LoginActivity::class.java)
                    cikisIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    startActivity(cikisIntent)
                    finish()
                }
            }

        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.anamenu,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {

        when(item?.itemId){

            R.id.cikisYap -> {
                cikisYap()
                return true
            }

            R.id.hesapAyarlari -> {
                var hesapIntent = Intent(this,HesapActivity::class.java)
                startActivity(hesapIntent)
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    private fun cikisYap() {
        FirebaseAuth.getInstance().signOut()
    }

    override fun onResume() {
        super.onResume()
        kullaniciyiKontrolEt()
        setKullaniciBilgileri()
    }

    private fun kullaniciyiKontrolEt() {
        var kullanici = FirebaseAuth.getInstance().currentUser
        if(kullanici == null){
            var cikisIntent = Intent(this@MainActivity,LoginActivity::class.java)
            cikisIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(cikisIntent)
            finish()
        }
    }

    override fun onStart() {
        super.onStart()
        FirebaseAuth.getInstance().addAuthStateListener(mAuthStateListener)
    }

    override fun onStop() {
        super.onStop()
        if(mAuthStateListener != null){
            FirebaseAuth.getInstance().removeAuthStateListener(mAuthStateListener)
        }
    }
}
