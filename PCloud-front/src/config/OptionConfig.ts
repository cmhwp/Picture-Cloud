/**
 * 选项配置
 */
import { listPictureTagCategoryUsingGet } from '@/api/pictureController'
import { ref } from 'vue'

const categoryList = ref<{ label: string; value: string }[]>([])
const tagList = ref<{ label: string; value: string; color: string }[]>([])

// 颜色列表
const colorList = ref([
  '#B5EAD7', // 薄荷绿
  '#FFB7B2', // 淡粉色
  '#C7CEEA', // 淡紫色
  '#FFDAC1', // 杏色
  '#E2F0CB', // 嫩绿色
  '#9AC4F8', // 天蓝色
  '#FF9AA2', // 珊瑚粉
  '#A8E6CF', // 青瓷绿
  '#DCD3FF', // 薰衣草紫
  '#F3D1DC', // 樱花粉
  '#BDEADD', // 湖水绿
  '#FFC09F', // 蜜桃色
  '#AEC6CF', // 灰蓝色
  '#D4F0F0', // 浅青色
  '#FFE5D9', // 奶茶色
  '#F4D03F', // 柠檬黄
  '#FFD580', // 橙色
  '#FF9B85', // 珊瑚橙
  '#B3E5FC', // 天青色
  '#FFDAB9', // 桃色
  '#D1F2EB', // 薄荷蓝
  '#FFD700', // 金色
])

// 异步获取分类列表
listPictureTagCategoryUsingGet().then((res) => {
  if (res.data?.code === 0 && res.data.data?.categoryList) {
    categoryList.value = res.data.data.categoryList.map((name) => ({ label: name, value: name }))
    tagList.value =
      res.data.data.tagList?.map((name) => ({
        label: name,
        value: name,
        color: colorList.value[Math.floor(Math.random() * colorList.value.length)],
      })) ?? []
  }
})

export { categoryList, tagList, colorList }
