Infuser
=======
Object injecting for classes where there is single type in contructor.
It is a light weight library which has the same functions with Dagger,
and is easily customized.

* Besides empty constructor,it also supports array of basic data types.Including `int` `long` `float` `double` `char` `String`.
* Custom your constructor as you like.

```java
class MainActivity extends Activity{

      @InfuseString("Dancer Kitty")
      public String name;

      @Infuse
      public Singer singer1;

      @InfuseInt({18,180})
      public Singer singer2;

      @Override
      public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        Infuser.bind(this);
        //DO WHAT YOU WANT WITH THESE OBJECTS
       .....
       }
}
```
Yes,Infuser is sharper than ButterKnife and Dagger.

Library projects
----------------
To use Infuser in a library,add the plugin to your repositories:
```groovy
 repositories {
        maven {
            url "https://dl.bintray.com/lxt318/infuser"
        }
    }
```

and then apply it in your module:
```groovy
    implementation 'com.lure.infuser:library:1.0.0'
    api 'com.lure.infuser:annotation:1.0.0'
    annotationProcessor 'com.lure.infuser:compiler:1.0.0'
```

License
-------

    Copyright 2013 Lure Lv

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.

