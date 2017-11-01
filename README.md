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