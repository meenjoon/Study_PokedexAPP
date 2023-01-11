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

import android.os.Bundle
import androidx.activity.viewModels
import androidx.annotation.VisibleForTesting
import com.skydoves.bindables.BindingActivity
import com.skydoves.pokedex.R
import com.skydoves.pokedex.databinding.ActivityMainBinding
import com.skydoves.transformationlayout.onTransformationStartContainer
import dagger.hilt.android.AndroidEntryPoint

//우선 PokedexApp 클래스에 @HiltApplication으로 hilt로 설정되어 있다.
// Application에 @HiltApplication 으로 hilt를 설정하면 @AndroidEntryPoint 주석이 있는 다른 클래스에 종속항목을 제공할수 있다
@AndroidEntryPoint // Hilt객체를 주입할 대상에게 의존성 주입, MainActivity에서 mainViewModel을 Hilt로 부터 제공받기 때문에 종속성이 해결되었다.
class MainActivity : BindingActivity<ActivityMainBinding>(R.layout.activity_main) { // 데이터 바인딩을 사용을 위한 MainActivity 클래스(첫 화면) , BindingActivity 클래스 타입을 따르며 데이터 바인딩을 사용하여 레이아웃(R.layout.activity_main)과 연결해줌
  //기본으로 액티비티가 상속받아 사용하는 AppCompatActivity()를 상속받음

  @get:VisibleForTesting
  // private으로 선언시 테스트코드에서 호출할 수 없기때문에 private 키워드를 제외하였을 때 다른 코드에서 접근할 수 없도록 해당 annotation을 추가하여 테스트 코드가 아닌 다른 곳에서 호출할 수 없다는 것을 나타냄
  //@Retention(class)로 만들었기 때문에 컴파일러가 컴파일에서는 어노테이션의 메모리를 가져가지만 실질적으로 런타임시에는 사라지게 된다. 즉, 리플렉션으로 선언된 어노테이션 데이터를 가져올 수 없다.

  //viewModel을 생성하는데 MainViewModel에서 정의된 viewModels()의 모든 메서드를 viewModel에 위임한다.
  internal val viewModel: MainViewModel by viewModels() //다른 클래스의 기능을 사용하되 그 기능을 변경하지 않기 위해 by 위임을 사용함

  override fun onCreate(savedInstanceState: Bundle?) {
    onTransformationStartContainer() // 직접 작성한 메서드
    super.onCreate(savedInstanceState)
    binding { //위에서 클래스 타입을 정의를 할 때 사용한 BindingActivity 클래스에서 사용된다.
      adapter = PokemonAdapter() //  PokemonAdapter 클래스를 데이터 바인딩을 통해 레이아웃 파일(xml)의 adapter과 연결한다.

      vm = viewModel //생성한 viewModel 객체를  데이터 바인딩을 통해 레이아웃 파일(xml)의 vm과 연결한다.
    }
  }
}
