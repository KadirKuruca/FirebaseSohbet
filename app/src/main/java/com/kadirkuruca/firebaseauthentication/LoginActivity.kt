package com.kadirkuruca.firebaseauthentication

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        tvKayit.setOnClickListener {
            intent = Intent(this@LoginActivity, RegisterActivity::class.java)
            startActivity(intent)
        }

        btn_giris.setOnClickListener {
            if(etLoginMail.text.isNotEmpty() && etLoginSifre.text.isNotEmpty()){
                progressBarGoster()

                FirebaseAuth.getInstance().signInWithEmailAndPassword(etLoginMail.text.toString(),etLoginSifre.text.toString())
                        .addOnCompleteListener(object : OnCompleteListener<AuthResult>{
                            override fun onComplete(p0: Task<AuthResult>) {

                                if(p0.isSuccessful){
                                    progressBarGizle()
                                    Toast.makeText(this@LoginActivity,"Giriş Başarılı : "+FirebaseAuth.getInstance().currentUser?.email,Toast.LENGTH_SHORT).show()
                                    FirebaseAuth.getInstance().signOut()
                                }
                                else{
                                    Toast.makeText(this@LoginActivity,"Giriş Yaparken Hata Oluştu.\n"+p0.exception?.message,Toast.LENGTH_SHORT).show()
                                    progressBarGizle()
                                }
                            }

                        })

            }
            else{
                Toast.makeText(this@LoginActivity,"Alanları Boş Bırakmayınız.",Toast.LENGTH_SHORT).show()
            }

        }
    }

    private fun progressBarGoster(){
        progressBarLogin.visibility = View.VISIBLE
    }

    private fun progressBarGizle(){
        progressBarLogin.visibility = View.INVISIBLE
    }
}
