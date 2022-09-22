/*
 * Copyright (C) 2020 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and 
 * limitations under the License.
 */

package com.example.android.unscramble.ui.game

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.android.unscramble.R
import com.example.android.unscramble.databinding.GameFragmentBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

/**
 * Fragment where the game is played, contains the game logic.
 */
class GameFragment : Fragment() {

    // untuk  binding ke layout game_fragment.xml
    private lateinit var binding: GameFragmentBinding

    //  Membuat fragmen ulang,lalu  menginstansi GameViewModel yang sama dengan fragmen pertama.
    private val viewModel: GameViewModel by viewModels()

    //    untuk memperbarui tampilan teks skor dan jumlah kata.
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        //     untuk binding ke layut game_fragment dan mengambalikan instan object
        binding = DataBindingUtil.inflate(inflater, R.layout.game_fragment, container, false)
        return binding.root
    }

    //  Untuk menyiapkan pemroses klik tombol dan mengupdate UI
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // untuk akses ke file layout dan menyetel viewModel untuk menghubungkan data
        binding.gameViewModel = viewModel
        binding.maxNoOfWords = MAX_NO_OF_WORDS

        //  Untuk pembaruan livedata
        binding.lifecycleOwner = viewLifecycleOwner

        // membaca aktivitas tombol Submit dan Skip.
        binding.submit.setOnClickListener { onSubmitWord() }
        binding.skip.setOnClickListener { onSkipWord() }
    }

    private fun onSubmitWord() {
        val playerWord = binding.textInputEditText.text.toString()

        //  mengecek kata dan mempebarui score jika jawaban benar
        if (viewModel.isUserWordCorrect(playerWord)) {
            setErrorTextField(false)
            if (!viewModel.nextWord()) {
                //  Menampilkan dialog score, jika game telah usai
                showFinalScoreDialog()
            }
        } else {
            setErrorTextField(true)
        }
    }

    //   Skip kata dan tanpa update score
    private fun onSkipWord() {
        if (viewModel.nextWord()) {
            setErrorTextField(false)
        } else {
            showFinalScoreDialog()
        }
    }

    //    Untuk menampilkan alert atau dialog score
    private fun showFinalScoreDialog() {
        MaterialAlertDialogBuilder(requireContext())
                .setTitle(getString(R.string.congratulations))
                .setMessage(getString(R.string.you_scored, viewModel.score.value))
                .setCancelable(false)
                .setNegativeButton(getString(R.string.exit)) { _, _ ->
                    exitGame()
                }
                .setPositiveButton(getString(R.string.play_again)) { _, _ ->
                    restartGame()
                }
                .show()
    }

    // Inisialisasi ulang data di ViewModel dan perbarui tampilan dengan data baru, untuk
    private fun restartGame() {
        viewModel.reinitializeData()
        setErrorTextField(false)
    }


    //  Untuk keluar dari game
    private fun exitGame() {
        activity?.finish()
    }

    //   Menampilkan pesan error jika kata salah
    private fun setErrorTextField(error: Boolean) {
        if (error) {
            binding.textField.isErrorEnabled = true
            binding.textField.error = getString(R.string.try_again)
        } else {
            binding.textField.isErrorEnabled = false
            binding.textInputEditText.text = null
        }
    }
}
