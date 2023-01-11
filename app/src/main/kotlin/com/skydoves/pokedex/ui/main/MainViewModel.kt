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

import androidx.annotation.MainThread
import androidx.databinding.Bindable
import androidx.lifecycle.viewModelScope
import com.skydoves.bindables.BindingViewModel
import com.skydoves.bindables.asBindingProperty
import com.skydoves.bindables.bindingProperty
import com.skydoves.core.data.repository.MainRepository
import com.skydoves.pokedex.core.model.Pokemon
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel // Hilt에게 해당 컴포넌트가 ViewModel임을 알려준다.
// Dagger2 Hilt를 통해 ViewModel에 의존성 주입 ==> ViewModel에 의존성 주입을 하게 되면
// 기존의 ViewModelProvider.Factory를 구현하기 위해서는 직접 팩토리를 생성해주어야 하기 때문에 많은 보일러 코드가 생기는데 이것을 해결해줌
// 이러한 기능을 멀티바인딩 기능이라고함 즉, ViewModelProvider.Factory를 매번 구현하지 않고도 사용할 수 있다.
class MainViewModel @Inject constructor( //해당 컴포넌트를 생성하는데 어떤 다른 종속성이 필요한지 알려준다. 여기서는 Hilt가 NoteRepository를 제공하는 법을 알기 때문에 사용이 가능하다.
  private val mainRepository: MainRepository
) : BindingViewModel() { // UI에 변경사항을 알릴 수 있는 방법을 제공하는 ViewModel이다. 안드로이드에서 제공하는것이 아닌 직접 커스텀한 ViewModel이다.

  @get:Bindable
  var isLoading: Boolean by bindingProperty(false)
    private set

  @get:Bindable
  var toastMessage: String? by bindingProperty(null)
    private set

  //Flow : 값을 순차적으로 내보내고 정상적으로 또는 예외적으로 완료되는 비동기 데이터 스트림
  //StateFlow : 현재 상태(State)를 관찬하는데 적합한 flow이다. 상태를 다루기 때문에 생성시 초기값이 필요하고 값 접근이 가능하며, .value로 다이렉트로 접근 가능하다.
  //SharedFlow : 이벤트를 관찰하는데 적합한 flow이다.
  private val pokemonFetchingIndex: MutableStateFlow<Int> = MutableStateFlow(0) //초기값이 0인 StateFlow변수 생성


  //flatMapLastest를 사용하여 flow에서 발행된 데이터를 변환하는 도중 새로운 데이터가 발행될 경우, 변환 로직을 취소하고 새로운 데이터를 사용해 변환을 수행한다.
  private val pokemonListFlow = pokemonFetchingIndex.flatMapLatest { page ->
    mainRepository.fetchPokemonList( // 클린 아키텍처의 데이터 계층에서의 repository에 있는 mainRepository 클래스의 fetchPokemonList를 바꾼다.
      page = page,
      onStart = { isLoading = true }, //isLoading이 true라면 onStart
      onComplete = { isLoading = false }, //isLoading이 false라면 onComplete
      onError = { toastMessage = it }
    )
  }

  @get:Bindable
  val pokemonList: List<Pokemon> by pokemonListFlow.asBindingProperty(viewModelScope, emptyList()) // 클린 아키텍처의 model 계층에서 만들어졌고, data class이다.

  init {
    Timber.d("init MainViewModel")
  }

  @MainThread
  fun fetchNextPokemonList() {
    if (!isLoading) { // isLoading이 false라면
      pokemonFetchingIndex.value++
    }
  }
}
