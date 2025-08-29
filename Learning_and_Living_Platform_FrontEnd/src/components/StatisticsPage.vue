<template>
  <div style="display: flex; justify-content: center;">
  <div class="line centre-panel centre-expand shadow-act glass">
    <div class="chart">
      <Card class="cardScatter">
        <template #title><p style="font-weight: bold;font-size: 20px;">统计数据</p></template>
        <div style="font-size: 16px;margin-left: 10px;">
          <NumberInfo title="当前在线人数" status="down">
              <template #total>
                  <CountUp :end="statistics.online" :duration="4" />
              </template>
          </NumberInfo>
          <NumberInfo title="已注册用户数" status="down">
              <template #total>
                  <CountUp :end="statistics.totalUser" :duration="4" />
              </template>
          </NumberInfo>
          <NumberInfo title="帖子总数" status="down">
              <template #total>
                  <CountUp :end="statistics.totalPost" :duration="4" />
              </template>
          </NumberInfo>
          <NumberInfo title="资源总数" status="down">
              <template #total>
                  <CountUp :end="statistics.totalRes" :duration="4" />
              </template>
          </NumberInfo>
        </div>
      </Card>
      <Card class="cardLine"><v-chart class="setLine" :option="regLine" autoresize /></Card>
    </div>
    <div class="chart">
      <Card class="cardPie">
        <v-chart class="setPie" :option="pie" autoresize />
      </Card>
      <Card ref="barChart" class="cardBar">
        <v-chart class="setBar" :option="bar" />
      </Card>
    </div>
    <div class="chart">
      <Card class="cardLine">
        <v-chart class="setLine" :option="line" autoresize />
      </Card>
      <Card class="cardScatter"><v-chart class="setScatter" :option="scatter" autoresize /></Card>
    </div>
  </div>
  </div>
</template>

<script setup>

import { use } from 'echarts/core';
import { get } from "@/net"
import '@/assets/panel.css'
import { CanvasRenderer } from 'echarts/renderers';
import { 
  LineChart,
  BarChart,
  PieChart,
  ScatterChart,
  RadarChart,
  GaugeChart,
  ParallelChart,
} from 'echarts/charts';
import {
  GridComponent,
  ToolboxComponent,
  TooltipComponent,
  TitleComponent,
  LegendComponent,
  DataZoomComponent,
  VisualMapComponent,
} from 'echarts/components';
import VChart, { THEME_KEY } from 'vue-echarts';
import { Snackbar } from '@varlet/ui'
import { ref, provide } from 'vue';
import { reactive } from 'vue';

use([
  CanvasRenderer,
  PieChart,
  BarChart,
  LineChart,
  RadarChart,
  GaugeChart,
  ScatterChart,
  ParallelChart,
  TitleComponent,
  TooltipComponent,
  LegendComponent,
  GridComponent,
  VisualMapComponent,
  ToolboxComponent,
  DataZoomComponent
]);

provide(THEME_KEY);

const statistics = reactive({
  online: 0,
  totalUser: 0,
  totalPost: 0,
  totalRes: 0
})


function getOverall() {
  get('/statistic/getOverallFigure',
    (mes) => {
      console.log(mes)
      statistics.online = mes.numOfOnlineUser
      statistics.totalPost = mes.numOfPost
      statistics.totalRes = mes.numOfResource
      statistics.totalUser = mes.numOfUser
    }
  )
  
}

//注册人数统计
const regLine = ref({
  title: {
    text: '注册用户统计',
    left: 'center'
  },
  tooltip: {
    trigger: 'axis',
    axisPointer: {
      animation: false
    }
  },
  dataZoom: [
    {
      top:'10%',
      show: true,
      realtime: true,
      start: 30,
      end: 70,
      xAxisIndex: [0, 1]
    }
  ],
  grid: [
    {
      top: '18%',
      left: 60,
      right: 50,
      height: '72%'
    },
  ],
  xAxis: [
    {
      type: 'category',
      boundaryGap: false,
      axisLine: { onZero: true },
      data: []
    }
  ],
  yAxis: [
    {
      name: '人',
      type: 'value',
    }
  ],
  series: [
    {
      type: 'line',
      symbolSize: 8,
      // prettier-ignore
      data: []
    }
  ]
});

function setRegLine() {
  get('/statistic/userRegisterCountByDays',
    (mes) => {
      regLine.value.xAxis[0].data = mes.days
      regLine.value.series[0].data = mes.count
        }
    )
}

//资源学科统计
const pie = ref({
  title: {
    text: "资源学科统计",
    left: "center"
  },
  tooltip: {
    trigger: 'item'
  },
  legend: {
    top: '10%',
    left: 'center'
  },
  series: [
    {
      name: '资源学科',
      type: 'pie',
      top:'30%',
      radius: ['50%', '65%'],
      avoidLabelOverlap: false,
      itemStyle: {
        borderRadius: 10,
        borderColor: '#fff',
        borderWidth: 2
      },
      label: {
        show: false,
        position: 'center'
      },
      emphasis: {
        label: {
          show: true,
          fontSize: 40,
          fontWeight: 'bold'
        }
      },
      labelLine: {
        show: false
      },
      data: [
        { value: 0, name: '理学工学' },
        { value: 0, name: '农学' },
        { value: 0, name: '外语' },
        { value: 0, name: '经济管理' },
        { value: 0, name: '计算机' },
        { value: 0, name: '音乐与艺术' },
        { value: 0, name: '心理学' },
        { value: 0, name: '文史哲法' },
        { value: 0, name: '医学与保健' },
        { value: 0, name: '教育教学' }
      ]
    }
  ]
});

function setPieData() {
  get('/statistic/resourceCountBySubject',
    (mes) => {
        for (var i = 0; i < 8; i++){
          pie.value.series[0].data[i].value = mes[i]
        }
      }
  )
}

//资源统计
const resBarDate = ref([])

const barChart = ref() 

const bar = ref({
  title: {
    text: "资源统计",
    left: "center"
  },
  tooltip: {
    trigger: 'axis',
    axisPointer: {
      // Use axis to trigger tooltip
      type: 'shadow' // 'shadow' as default; can also be 'line' or 'shadow'
    }
  },
  legend: {
    top: "10%"
  },
  grid: {
    top: '20%',
    left: '3%',
    right: '4%',
    bottom: '3%',
    containLabel: true
  },
  xAxis: {
    type: 'value'
  },
  yAxis: {
    type: 'category',
    data: ['理学工学', '农学', '外语', '经济管理', '计算机', '音乐与艺术', '心理学', '文史哲法', '医学与保健', '教育教学']
  },
  series: [
    {
      name: '文档',
      type: 'bar',
      stack: 'total',
      label: {
        show: true
      },
      emphasis: {
        focus: 'series'
      },
      data: resBarDate.value[0]
    },
    {
      name: '源代码',
      type: 'bar',
      stack: 'total',
      label: {
        show: true
      },
      emphasis: {
        focus: 'series'
      },
      data: resBarDate.value[1]
    },
    {
      name: '视频',
      type: 'bar',
      stack: 'total',
      label: {
        show: true
      },
      emphasis: {
        focus: 'series'
      },
      data: resBarDate.value[2]
    },
    {
      name: '图片',
      type: 'bar',
      stack: 'total',
      label: {
        show: true
      },
      emphasis: {
        focus: 'series'
      },
      data: resBarDate.value[3]
    },
    {
      name: '音频',
      type: 'bar',
      stack: 'total',
      label: {
        show: true
      },
      emphasis: {
        focus: 'series'
      },
      data: resBarDate.value[4]
    },
    {
      name: '其他',
      type: 'bar',
      stack: 'total',
      label: {
        show: true
      },
      emphasis: {
        focus: 'series'
      },
      data: resBarDate.value[5]
    }
  ]
});

function setResBarData() {
  get('/statistic/resourceClassificationStatistics',
        (mes) => {
          for (var i = 0; i < 6; i++){
            bar.value.series[i].data = mes[i]
          }
        }
    )
}

//资源上传下载量
const line = ref({
  title: {
    text: '资源上传下载量',
    left: 'center'
  },
  tooltip: {
    trigger: 'axis',
    axisPointer: {
      animation: false
    }
  },
  legend: {
    top: "5%",
    data: ['上传', '下载'],
    left: 10
  },
  dataZoom: [
    {
      top:'10%',
      show: true,
      realtime: true,
      start: 30,
      end: 70,
      xAxisIndex: [0, 1]
    }
  ],
  grid: [
    {
      top: '18%',
      left: 60,
      right: 50,
      height: '72%'
    },
  ],
  xAxis: [
    {
      type: 'category',
      boundaryGap: false,
      axisLine: { onZero: true },
      data: []
    }
  ],
  yAxis: [
    {
      name: '次数',
      type: 'value',
    }
  ],
  series: [
    {
      name: '下载',
      type: 'line',
      symbolSize: 8,
      // prettier-ignore
      data: []
    },
    {
      name: '上传',
      type: 'line',
      // xAxisIndex: 1,
      // yAxisIndex: 1,
      symbolSize: 8,
      // prettier-ignore
      data: []
    }
  ]
});

function setLineData() {
  get('/statistic/resDownsAndUpsByDays',
    (mes) => {
      line.value.xAxis[0].data = mes.days
      line.value.series[0].data = mes.downloads
      line.value.series[1].data = mes.uploads
        }
    )
}

//帖子统计

const schema1 = [
  { name: 'date', index: 0, text: '发帖时间' },
  { name: 'HOTindex', index: 1, text: ' 热度指数' },
  { name: 'level', index: 1, text: '作者等级' },
  { name: 'browse', index: 3, text: '浏览量' },
  { name: 'like', index: 4, text: '点赞量' },
  { name: 'floor', index: 5, text: '楼层数' },
];

const itemStyle = {
  opacity: 0.8,
  shadowBlur: 10,
  shadowOffsetX: 0,
  shadowOffsetY: 0,
  shadowColor: 'rgba(0,0,0,0.3)'
};

const scatter = ref({
  title: {
    text: "热帖统计",
    left: "center"
  },
  //color: ['#FFB6C1'],
  grid: {
    left: '10%',
    right: 150,
    top: '18%',
    bottom: '10%'
  },
  tooltip: {
    backgroundColor: 'rgba(255,255,255,0.7)',
    formatter: function (param) {
      var value = param.value;
      // prettier-ignore
      return '<div style="border-bottom: 1px solid rgba(255,255,255,.3); font-size: 18px;padding-bottom: 7px;margin-bottom: 7px">'
        + '标题：' +value[6]
        + '</div>'
        + schema1[1].text + '：' + value[1] + '<br>'
        + schema1[2].text + '：' + value[2] + '<br>'
        + schema1[3].text + '：' + value[3] + '<br>'
        + schema1[4].text + '：' + value[4] + '<br>'
    }
  },
  xAxis: {
    data: [],
    name: '发帖时间',
    nameGap: 16,
    nameTextStyle: {
      fontSize: 16
    },
    splitLine: {
      show: false
    }
  },
  yAxis: {
    type: 'value',
    name: '热度',
    nameLocation: 'end',
    nameGap: 20,
    nameTextStyle: {
      fontSize: 16
    },
    splitLine: {
      show: false
    }
  },
  visualMap: [
    {
      left: 'right',
      top: '10%',
      dimension: 4,
      min: 0,
      max: 250,
      itemWidth: 30,
      itemHeight: 120,
      calculable: true,
      precision: 0.1,
      text: ['圆形大小：点赞量'],
      textGap: 30,
      inRange: {
        symbolSize: [10, 70]
      },
      outOfRange: {
        symbolSize: [10, 70],
        color: ['rgba(255,255,255,0.4)']
      },
      controller: {
        inRange: {
          color: ['#c23531']
        },
        outOfRange: {
          color: ['#999']
        }
      }
    },
    {
      left: 'right',
      bottom: '5%',
      dimension: 3,
      min: 0,
      max: 1000,
      itemHeight: 120,
      text: ['明暗：浏览量'],
      textGap: 30,
      inRange: {
        colorLightness: [0.9, 0.5]
      },
      outOfRange: {
        color: ['rgba(255,255,255,0.4)']
      },
      controller: {
        inRange: {
          color: ['#c23531']
        },
        outOfRange: {
          color: ['#999']
        }
      }
    }
  ],
  series: [
    {
      name: '帖子',
      type: 'scatter',
      itemStyle: itemStyle,
      data: []
    }
  ]
})



function setPostsData() {
  get('/statistic/hotPostByDays',
    (mes) => {
      //console.log(mes)
      scatter.value.xAxis.data = mes.date
      scatter.value.series[0].data = mes.data
    }
    )
}

function getStatistics() {
  getOverall()
  setPieData()
  setResBarData()  
  setLineData()
  setRegLine()
  setPostsData()
}

getStatistics()
</script>



<style scoped>
.line {
  display: block;
}

.chart {
  display: flex;
  margin: 10px 10px;
}

.cardPie {
  width: 350px;
  margin: 20px 20px;
}

.cardBar {
  width: 650px;
  margin: 20px 20px;
}

.cardLine {
  width: 500px;
  margin: 20px 20px;
}

.cardRadar {
  width: 350px;
  margin: 20px 20px;
}

.cardPara {
  width: 1050px;
  margin: 20px 20px;
}

.cardScatter {
  width: 500px;
  margin: 20px 20px;
}

.setPie {
  width: 300px;
  height: 400px;
}

.setBar {
  width: 600px;
  height: 400px;
}

.setLine {
  width: 470px;
  height: 500px;
}

.setRadar {
  width: 300px;
  height: 500px;
}

.setPara {
  width: 1000px;
  height: 450px;
}

.setScatter {
  width: 470px;
  height: 500px;
}

</style>
