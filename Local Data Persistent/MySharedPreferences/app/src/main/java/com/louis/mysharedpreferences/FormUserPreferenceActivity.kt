package com.louis.mysharedpreferences

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.louis.mysharedpreferences.databinding.ActivityFormUserPreferenceBinding

class FormUserPreferenceActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var userModel: UserModel
    private lateinit var binding: ActivityFormUserPreferenceBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFormUserPreferenceBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnSave.setOnClickListener(this)

        userModel = intent.getParcelableExtra<UserModel>("USER") as UserModel
        val formType = intent.getIntExtra(EXTRA_TYPE_FORM, 0)

        var actionBarTitle = ""
        var btnTitle = ""

        when (formType) {
            TYPE_ADD -> {
                actionBarTitle = "Tambah Baru"
                btnTitle = "Simpan"
            }
            TYPE_EDIT -> {
                actionBarTitle = "Ubah"
                btnTitle = "Update"
                showPreferenceInForm()
            }
        }

        supportActionBar?.apply {
            title = actionBarTitle
            setDisplayHomeAsUpEnabled(true)
        }

        binding.btnSave.text = btnTitle
    }

    override fun onClick(view: View) {
        if (view.id == R.id.btn_save) {
            with(binding) {
                val name = edtName.text.toString().trim()
                val email = edtEmail.text.toString().trim()
                val age = edtAge.text.toString().trim()
                val phoneNo = edtPhone.text.toString().trim()
                val isLoveMU = rgLoveMu.checkedRadioButtonId == R.id.rb_yes

                if (name.isEmpty()) {
                    edtName.error = FIELD_REQUIRED
                    return
                }

                if (email.isEmpty()) {
                    edtEmail.error = FIELD_REQUIRED
                    return
                }

                if (!isValidEmail(email)) {
                    edtEmail.error = FIELD_IS_NOT_VALID
                    return
                }

                if (age.isEmpty()) {
                    edtAge.error = FIELD_REQUIRED
                    return
                }

                if (phoneNo.isEmpty()) {
                    edtPhone.error = FIELD_REQUIRED
                    return
                }

                if (!TextUtils.isDigitsOnly(phoneNo)) {
                    edtPhone.error = FIELD_DIGIT_ONLY
                    return
                }

                saveUser(name, email,age, phoneNo, isLoveMU)

                Intent().apply {
                    putExtra(EXTRA_RESULT, userModel)
                }.also {
                    setResult(RESULT_CODE, it)
                }

                finish()
                Toast.makeText(this@FormUserPreferenceActivity, "Data Tersimpan", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showPreferenceInForm() {
        with(binding) {
            edtName.setText(userModel.name)
            edtEmail.setText(userModel.email)
            edtAge.setText(userModel.age.toString())
            edtPhone.setText(userModel.phoneNumber)

            if (userModel.isLove) {
                rbYes.isChecked = true
            } else {
                rbNo.isChecked = true
            }
        }
    }

    private fun saveUser(name: String, email: String, age: String, phoneNo: String, isLoveMU: Boolean) {
        with(userModel) {
            this.name = name
            this.email = email
            this.age = Integer.parseInt(age)
            this.phoneNumber = phoneNo
            this.isLove = isLoveMU
        }

        UserPreference(this).apply {
            setUser(userModel)
        }
    }

    private fun isValidEmail(email: CharSequence) = android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()

    companion object {
        const val EXTRA_TYPE_FORM = "extra_type_form"
        const val EXTRA_RESULT = "extra_result"
        const val RESULT_CODE = 101

        const val TYPE_ADD = 1
        const val TYPE_EDIT = 2

        private const val FIELD_REQUIRED = "Field tidak boleh kosong"
        private const val FIELD_DIGIT_ONLY = "Hanya boleh terisi numerik"
        private const val FIELD_IS_NOT_VALID = "Email tidak valid"
    }
}