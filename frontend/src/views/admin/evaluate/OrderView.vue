<template>
  <a-modal v-model="show" title="订单详情" @cancel="onClose" :width="1000">
    <template slot="footer">
      <a-button key="back" @click="onClose" type="danger">
        关闭
      </a-button>
    </template>
    <div style="font-size: 13px;font-family: SimHei" v-if="orderData !== null">
      <div style="padding-left: 24px;padding-right: 24px;margin-bottom: 50px;margin-top: 50px">
        <a-steps :current="current" progress-dot size="small">
          <a-step title="未支付" />
          <a-step title="已支付" />
          <a-step title="退房中" />
          <a-step title="已完成" />
        </a-steps>
      </div>
      <a-row style="padding-left: 24px;padding-right: 24px;" v-if="userInfo != null">
        <a-col style="margin-bottom: 15px"><span style="font-size: 15px;font-weight: 650;color: #000c17">用户信息</span></a-col>
        <a-col :span="8"><b>用户编号：</b>
          {{ userInfo.code ? userInfo.code : '- -' }}
        </a-col>
        <a-col :span="8"><b>用户名称：</b>
          {{ userInfo.name ? userInfo.name : '- -' }}
        </a-col>
        <a-col :span="8"><b>联系方式：</b>
          {{ userInfo.phone }}
        </a-col>
      </a-row>
      <br/>
      <a-row style="padding-left: 24px;padding-right: 24px;">
        <a-col style="margin-bottom: 15px"><span style="font-size: 15px;font-weight: 650;color: #000c17">房间信息</span></a-col>
        <a-col :span="8"><b>房间名称：</b>
          {{ deviceInfo.name ? deviceInfo.name : '- -' }}
        </a-col>
        <a-col :span="8"><b>地址：</b>
          {{ deviceInfo.model ? deviceInfo.model : '- -' }}
        </a-col>
        <a-col :span="8"><b>区域：</b>
          {{ deviceInfo.brand ? deviceInfo.brand : '- -' }}
        </a-col>
      </a-row>
      <br/>
      <br/>
      <a-row style="padding-left: 24px;padding-right: 24px;">
        <a-col style="margin-bottom: 15px"><span style="font-size: 15px;font-weight: 650;color: #000c17">房间图片</span></a-col>
        <a-col :span="24">
          <a-upload
            name="avatar"
            action="http://127.0.0.1:9527/file/fileUpload/"
            list-type="picture-card"
            :file-list="fileList"
            @preview="handlePreview"
            @change="picHandleChange"
          >
          </a-upload>
          <a-modal :visible="previewVisible" :footer="null" @cancel="handleCancel">
            <img alt="example" style="width: 100%" :src="previewImage" />
          </a-modal>
        </a-col>
      </a-row>
      <br/>
      <a-row style="padding-left: 24px;padding-right: 24px;">
        <a-col style="margin-bottom: 15px"><span style="font-size: 15px;font-weight: 650;color: #000c17">订单信息</span></a-col>
        <a-col :span="8"><b>订单编号：</b>
          {{ orderData.code ? orderData.code : '- -' }}
        </a-col>
        <a-col :span="8"><b>开始借用时间：</b>
          {{ orderData.startDate ? orderData.startDate : '- -' }}
        </a-col>
        <a-col :span="8"><b>退房时间：</b>
          {{ orderData.endDate ? orderData.endDate : '- -' }}
        </a-col>
      </a-row>
      <br/>
      <a-row style="padding-left: 24px;padding-right: 24px;">
        <a-col :span="8"><b>租借小时：</b>
          {{ orderData.rentHour ? orderData.rentHour : '- -' }}
        </a-col>
        <a-col :span="8"><b>单价（元）：</b>
          {{ orderData.unitPrice ? orderData.unitPrice : '- -' }}
        </a-col>
        <a-col :span="8"><b>押金（元）：</b>
          {{ orderData.depositPrice ? orderData.depositPrice : '- -' }}
        </a-col>
      </a-row>
      <br/>
      <a-row style="padding-left: 24px;padding-right: 24px;">
        <a-col :span="8"><b>总价格：</b>
          {{ orderData.totalPrice ? (orderData.totalPrice + '元') : '- -' }}
        </a-col>
        <a-col :span="8"><b>退房时间：</b>
          {{ orderData.returnDate ?orderData.returnDate : '- -' }}
        </a-col>
        <a-col :span="8"><b>下单时间：</b>
          {{ orderData.createDate ?orderData.createDate : '- -' }}
        </a-col>
      </a-row>
      <br/>
      <br/>
      <a-row style="padding-left: 24px;padding-right: 24px;">
        <a-col style="margin-bottom: 15px"><span style="font-size: 15px;font-weight: 650;color: #000c17">评价信息</span></a-col>
        <a-col :span="6"><b>评价得分：</b>
          {{ orderData.score }}
        </a-col>
        <a-col :span="18"><b>评价备注：</b>
          {{ orderData.content ? orderData.content : '- -' }}
        </a-col>
      </a-row>
    </div>
  </a-modal>
</template>

<script>
import baiduMap from '@/utils/map/baiduMap'
import moment from 'moment'
moment.locale('zh-cn')
function getBase64 (file) {
  return new Promise((resolve, reject) => {
    const reader = new FileReader()
    reader.readAsDataURL(file)
    reader.onload = () => resolve(reader.result)
    reader.onerror = error => reject(error)
  })
}
export default {
  name: 'orderView',
  props: {
    orderShow: {
      type: Boolean,
      default: false
    },
    orderData: {
      type: Object
    }
  },
  computed: {
    show: {
      get: function () {
        return this.orderShow
      },
      set: function () {
      }
    }
  },
  data () {
    return {
      loading: false,
      fileList: [],
      previewVisible: false,
      previewImage: '',
      repairInfo: null,
      reserveInfo: null,
      durgList: [],
      logisticsList: [],
      userInfo: null,
      orderInfo: null,
      deviceInfo: null,
      current: 0
    }
  },
  watch: {
    orderShow: function (value) {
      if (value) {
        this.current = this.orderData.status
        if (this.orderData.typeImages) {
          this.imagesInit(this.orderData.typeImages)
        }
        this.queryOrderDetail(this.orderData.orderCode)
      }
    }
  },
  methods: {
    queryOrderDetail(orderCode) {
      this.$get(`/cos/rent-order-info/queryOrderDetail/${orderCode}`).then((r) => {
        this.orderInfo = r.data.orderInfo
        this.userInfo = r.data.userInfo
        this.deviceInfo = r.data.deviceInfo
      })
    },
    local (orderData) {
      baiduMap.clearOverlays()
      baiduMap.rMap().enableScrollWheelZoom(true)
      // eslint-disable-next-line no-undef
      let point = new BMap.Point(orderData.longitude, orderData.latitude)
      baiduMap.pointAdd(point)
      baiduMap.findPoint(point, 16)
      // let driving = new BMap.DrivingRoute(baiduMap.rMap(), {renderOptions:{map: baiduMap.rMap(), autoViewport: true}});
      // driving.search(new BMap.Point(this.nowPoint.lng,this.nowPoint.lat), new BMap.Point(scenic.point.split(",")[0],scenic.point.split(",")[1]));
    },
    imagesInit (images) {
      if (images !== null && images !== '') {
        let imageList = []
        images.split(',').forEach((image, index) => {
          imageList.push({uid: index, name: image, status: 'done', url: 'http://127.0.0.1:9527/imagesWeb/' + image})
        })
        this.fileList = imageList
      }
    },
    handleCancel () {
      this.previewVisible = false
    },
    async handlePreview (file) {
      if (!file.url && !file.preview) {
        file.preview = await getBase64(file.originFileObj)
      }
      this.previewImage = file.url || file.preview
      this.previewVisible = true
    },
    picHandleChange ({ fileList }) {
      this.fileList = fileList
    },
    onClose () {
      this.$emit('close')
    }
  }
}
</script>

<style scoped>

</style>
