/**
 * 选项配置
 */
import { listPictureTagCategoryUsingGet } from '@/api/pictureController'
import { ref } from 'vue'

const categoryList = ref<{ label: string; value: string }[]>([])
const tagList = ref<{ label: string; value: string; color: string }[]>([])

// 颜色列表
const colorList = ref([
  '#00FF87', // 荧光绿
  '#FF69B4', // 亮粉红
  '#87CEEB', // 天蓝色
  '#FFA500', // 橙色
  '#5DADE2', // 明亮蓝
  '#40E0D0', // 绿松石色
  '#FF6B6B', // 亮珊瑚红
  '#4DE680', // 明亮绿
  '#836FFF', // 亮紫色
  '#FFD700', // 金色
  '#00CED1', // 深青色
  '#FF7F50', // 珊瑚色
  '#6495ED', // 矢车菊蓝
  '#32CD32', // 酸橙绿
  '#FF69B4', // 热粉红
  '#FFA07A', // 亮鲑鱼色
  '#00FFFF', // 青色
  '#FF1493', // 深粉红
  '#FF4500', // 橙红色
  '#1E90FF', // 道奇蓝
  '#FF8C00', // 深橙色
])

// 异步获取分类列表
listPictureTagCategoryUsingGet().then((res) => {
  if (res.data?.code === 0 && res.data.data?.categoryList) {
    // 添加"全部"选项到分类列表开头
    categoryList.value = [
      { label: '全部', value: 'all' },
      ...res.data.data.categoryList.map((name) => ({ label: name, value: name })),
    ]

    tagList.value =
      res.data.data?.tagList?.map((name) => ({
        label: name,
        value: name,
        color: colorList.value[Math.floor(Math.random() * colorList.value.length)],
      })) ?? []
  }
})

export { categoryList, tagList, colorList }
