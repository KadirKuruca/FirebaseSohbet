package com.kadirkuruca.firebaseauthentication


import android.content.Context
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.fragment_sifre_sifirla_dialog.*
import kotlinx.android.synthetic.main.fragment_sifre_sifirla_dialog.view.*


class SifreSifirlaDialogFragment : DialogFragment() {


    lateinit var mContext : FragmentActivity

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        var view = inflater!!.inflate(R.layout.fragment_sifre_sifirla_dialog, container, false)
        mContext = activity

        view.btnSifreSifirlaIptal.setOnClickListener {
            dialog.dismiss()
        }

        view.btnSifreSifirlaGonder.setOnClickListener {

            FirebaseAuth.getInstance().sendPasswordResetEmail(etSifreGonderEmail.text.toString())
                    .addOnCompleteListener { task ->
                        if(task.isSuccessful){
                            Toast.makeText(mContext,"Şifre Sıfırlama Maili Gönderildi.",Toast.LENGTH_SHORT).show()

                        }else{
                            Toast.makeText(mContext,"Mail Gönderilirken Hata Oluştu\n"+task.exception?.message,Toast.LENGTH_SHORT).show()
                        }
                        dialog.dismiss()
                    }
        }

        return view
    }

}
