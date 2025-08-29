<!--这里创建一个vantajs的挂载区域-->
  <template>
    <div class="vanta_area" ref="Area">
      <div style="text-align: center;color: aliceblue;font-size: 40px;">
        <p>Hello!</p>
      </div>
    </div>
  </template>
  <script setup>
  //导入vanta.js和three.js，以及ref等hooks
  import * as THREE from 'three'
  import FOG from 'vanta/dist/vanta.fog.min'
  import {onMounted,onBeforeUnmount,ref} from 'vue'
  
  //使用ref引用挂载区域
  const Area=ref(null)
  //创建一个全局的变量来使用vanta.js
  /**
  *因为在vue2中，是使用this.vantaEffect来创建指定的3d动画模板的
  *但是vue3 setup中是没有this，所以要另外创建一个
  **/
  let vantaEffect=null;
  //在两个生命周期钩子内创建vantaEffect
  onMounted(()=>{
    // vantaEffect=CLOUDS2({
    //     el:Area.value,
    //     THREE:THREE,
    //     //如果需要改变样式，要写在这里
    //     //因为这里vantaEffect是没有setOptions这个方法的
    //     mouseControls: true,
    //     touchControls: true,
    //     gyroControls: false,
    //     minHeight: 200.00,
    //     minWidth: 200.00
    // })

    vantaEffect = FOG({
      el:Area.value,
      THREE:THREE,
      mouseControls: true,
      touchControls: true,
      gyroControls: false,
      minHeight: 200.00,
      minWidth: 200.00,
      highlightColor: 0xffffff,
      midtoneColor: 0xf9b3ab
    })
  })
  
  onBeforeUnmount(()=>{
    if(vantaEffect){
        vantaEffect.destroy()
    }
  })
  </script>
  <style scoped>
  .vanta_area {
  margin: 0;
  width:100vw;
  height:100vh;
  }
  </style>
  