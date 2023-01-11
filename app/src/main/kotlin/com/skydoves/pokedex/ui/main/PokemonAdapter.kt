/*
 * Designed and developed by 2022 skydoves (Jaewoong Eum)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.skydoves.pokedex.ui.main

import android.os.SystemClock
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.NO_POSITION
import com.skydoves.bindables.BindingListAdapter
import com.skydoves.bindables.binding
import com.skydoves.pokedex.R
import com.skydoves.pokedex.core.model.Pokemon
import com.skydoves.pokedex.databinding.ItemPokemonBinding
import com.skydoves.pokedex.ui.details.DetailActivity

class PokemonAdapter : BindingListAdapter<Pokemon, PokemonAdapter.PokemonViewHolder>(diffUtil) {

  private var onClickedAt = 0L

  //어떤 xml으로 뷰 홀더를 생성할 지 지정
  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PokemonViewHolder =
    parent.binding<ItemPokemonBinding>(R.layout.item_pokemon).let(::PokemonViewHolder)

  //뷰 홀더에 데이터 바인딩
  override fun onBindViewHolder(holder: PokemonViewHolder, position: Int) =
    holder.bindPokemon(getItem(position))

  //생성된 뷰 홀더에 값 지정
  inner class PokemonViewHolder constructor(
    private val binding: ItemPokemonBinding
  ) : RecyclerView.ViewHolder(binding.root) { //뷰바인딩을 이용하여 리사이클러뷰 뷰 홀더와 연결

    init {
      binding.root.setOnClickListener { //뷰 홀더 클릭 시에
        val position = bindingAdapterPosition.takeIf { it != NO_POSITION }
          ?: return@setOnClickListener
        val currentClickedAt = SystemClock.elapsedRealtime()
        if (currentClickedAt - onClickedAt > binding.transformationLayout.duration) {
          DetailActivity.startActivity(binding.transformationLayout, getItem(position))
          onClickedAt = currentClickedAt
        }
      }
    }

    fun bindPokemon(pokemon: Pokemon) { //데이터 바인딩과 리사이클러뷰 연결
      binding.pokemon = pokemon
      binding.executePendingBindings() // 데이텨 변경이 즉각적으로 수행되어야 하는 경우
    }
  }

  companion object { //전역 변수처럼 사용 할 수 있지만, 독립된 객체로 생각해야함
    private val diffUtil = object : DiffUtil.ItemCallback<Pokemon>() {

      override fun areItemsTheSame(oldItem: Pokemon, newItem: Pokemon): Boolean =
        oldItem.name == newItem.name

      override fun areContentsTheSame(oldItem: Pokemon, newItem: Pokemon): Boolean =
        oldItem == newItem
    }
  }
}
