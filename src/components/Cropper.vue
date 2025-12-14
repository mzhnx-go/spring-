<script setup>
import { reactive, inject, ref } from 'vue'
const onImageUploaded = inject('onImageUploaded', null);
// 载图组件
const axios = inject('axios')
// 裁剪参数
const option = reactive({
    img: '',//裁剪图片的地址
    fixedBox: true,//是否固定裁剪框大小
    canMoveBox: false,//裁剪框能否拖动
    fixedNumber: [373, 207],//截图框的宽高比例
    outputSize: 1,//裁剪生成图片的质量:0.1~1
    outputType: 'jpeg',//裁剪生成图片的格式: jpeg, png, webp
    autoCrop: true,//是否默认生成截图框
    // 只有自动裁剪开启 宽度高度才生效
    autoCropWidth: 373,
    autoCropHeight: 207,
    // 真实的输出宽高
    infoTrue: true,//true 为展示真实输出图片宽高 false 展示看到的截图框宽高
    original: true,//上传图片展示原图, 不会缩放
    centerBox: true,//截图框是否被限制在图片里面
    enlarge: 1,//图片根据截图框输出比例倍数
})

const data = reactive({
    file: null,
    f: null,
    innerHTML: "",
    freshCropper: true,//是否要刷新当前组件，使得组件恢复初始状态
    thumbnail: "",//上传至服务器端的图片的url
    isEditor: false//是否为编辑文章
})
//变换图片文件时调用（固定套路）
function uploadImg(event) {
    data.file = event.target.files[0];
    const item = {
        name: data.file.name,
        size: data.file.size,
        file: data.file
    };
    html5Reader(data.file, item);
    data.isEditor = false;
    document.getElementById('fade').style.display = 'block';
    document.getElementById('upload2').value = '';
}

// 将图片文件转成BASE64格式（固定套路）
function html5Reader(file, item) {
    const reader = new FileReader();
    reader.onload = e => {
        option.img = e.target.result;
    };
    reader.readAsDataURL(file);
}
//取消上传（固定套路）
function cancelUpload() {
    document.getElementById('fade').style.display = 'none'
}
//myCropper代表页面上的VueCropper组件
const myCropper = ref(null)
let lastEditRange = ref(0)

// 点击确定按钮，执行裁剪后文件上传逻辑
// 核心：裁剪图片 → 转文件对象 → 表单提交 → 富文本插入图片
function determineUpload($event) {
    // 调用裁剪组件获取裁剪后的Base64格式图片数据
    myCropper.value.getCropData((cropData) => {
        // 将Base64数据转换为文件对象
        let theBlob = base64ToFile(cropData, data.file.name);
        // 构建FormData表单
        let formData = new FormData();
        formData.append("file", theBlob.file, theBlob.name);

        // 设置请求头
        let config = {
            headers: {
                "Content-Type": "multipart/form-data"
            }
        };

        // 发送POST请求上传文件到服务器
        axios.post("/api/article/upload", formData, config)
            .then(res => {
                // 接口响应成功（HTTP状态码200）
                if (res.status === 200) {
                    try {
                        let imageUrl = res.data.map.url;

                        // 确保 url 是绝对路径或相对路径，避免重复拼接
                        if (imageUrl && !imageUrl.startsWith('http')) {
                            // 如果以 /api 或 api 开头，则直接使用
                            if (imageUrl.startsWith('/api') || imageUrl.startsWith('api')) {
                                imageUrl = '/' + imageUrl; // 统一为 /api/...
                            } else {
                                // 否则添加 /api 前缀
                                imageUrl = '/api/' + imageUrl;
                            }
                        }

                        // 获取富文本编辑器DOM元素
                        var editor = document.getElementById('text');
                        // 保存服务器返回的图片访问地址
                        data.thumbnail = imageUrl;
                        // 聚焦编辑器，保证光标定位
                        editor.focus();

                        // 恢复上传前的光标位置
                        var lastEditRangel = lastEditRange.value;
                        var selection = window.getSelection();
                        if (lastEditRangel) {
                            selection.removeAllRanges(); // 清除现有光标
                            selection.addRange(lastEditRangel); // 还原之前的光标位置
                        }

                        // 向富文本编辑器插入图片
                        document.execCommand('InsertImage', false, data.thumbnail);
                        // 记录插入图片后的新光标位置
                        lastEditRange.value = selection.getRangeAt(0);
                    } catch (e) {
                        // 捕获编辑器操作异常
                        console.log("富文本插入图片异常：", e);
                    }
                } else {
                    // 接口响应失败
                    console.log("文件上传失败，HTTP状态码：" + res.status);
                }
            })
            .catch(err => {
                // 捕获接口请求异常
                console.error("文件上传请求异常：", err);
            });

        // 隐藏上传/裁剪的遮罩层
        document.getElementById('fade').style.display = 'none';
    });
}

// 将Base64格式数据转换为文件对象（前端图片处理常用工具函数）
function base64ToFile(dataurl, filename) {
    var arr = dataurl.split(','),
        mime = arr[0].match(/:(.*?);/)[1],
        bstr = atob(arr[1]),
        n = bstr.length,
        u8arr = new Uint8Array(n);
    while (n--) {
        u8arr[n] = bstr.charCodeAt(n);
    }
    let theBlob = new Blob([u8arr], { type: mime });
    theBlob.lastModifiedDate = new Date();
    theBlob.name = filename;
    return {
        file: theBlob,
        name: filename
    }
}

// 记录富文本编辑器的光标位置
function getLastEditRange() {
    // 获取当前文本选择对象
    var selection = window.getSelection()
    // 保存当前光标对应的选区范围，用于后续恢复光标位置
    lastEditRange.value = selection.getRangeAt(0)
}

// 控制裁剪图片顺时针旋转90度
function rotateRight() {
    // 调用VueCropper组件的内置旋转方法
    myCropper.value.rotateRight()
}

function onBlur($event) {
    // 失去焦点时，同步富文本编辑区域的内容到data中
    data.innerHTML = $event.target.innerHTML
}

// 注入父组件提供的“freshCropper”方法（用于刷新裁剪组件）
const freshCropper = inject("freshCropper")

// 提供给父组件调用：获取当前上传到服务器的图片地址
const getThumbnail = () => {
    return data.thumbnail
}

function clearData() {
    // 清空图片相关 data
    data.thumbnail = "";
    data.isEditor = false;

    // 用父组件注入的方法 refresh cropper 组件 state
    if (freshCropper && typeof freshCropper === 'function') {
        // 使用 setTimeout 免立即执行，避免递归
        setTimeout(() => {
            freshCropper();
        }, 0);
    }
}

// 父组件编辑文章时调用：设置图片地址并切换为编辑状态
function setThumbnail(thumbnail) {
    data.thumbnail = thumbnail
    data.isEditor = true
}

// 暴露指定方法给父组件，使父组件可调用当前组件的功能
defineExpose({
    getThumbnail,
    clearData,
    setThumbnail
})
</script>

<template>
    <!-- 遮罩层：由data.freshCropper控制显示，用于覆盖页面并展示裁剪窗口 -->
    <div id="fade" class="black_overlay" v-if="data.freshCropper">
        <!-- 裁剪窗口容器 -->
        <div id="light" class="white_content">
            <div class="test">
                <!-- VueCropper裁剪组件：绑定之前定义的option配置项 -->
                <VueCropper ref="myCropper" :img="option.img" :fixedBox="option.fixedBox"
                    :canMoveBox="option.canMoveBox" :fixedNumber="option.fixedNumber" :outputSize="option.outputSize"
                    :outputType="option.outputType" :infoTrue="option.infoTrue" :autoCrop="option.autoCrop"
                    :autoCropWidth="option.autoCropWidth" :autoCropHeight="option.autoCropHeight"
                    :centerBox="option.centerBox" :original="option.original" :enlarge="option.enlarge" />
            </div>

            <!-- 裁剪控制按钮区域 -->
            <div class="cropper-control">
                <el-row justify="end">
                    <el-col :span="24">
                        <!-- 操作提示文本 -->
                        <span style="margin-right:10px">滚动鼠标滚轮可以缩放图片</span>
                        <!-- 取消上传：绑定cancelUpload方法 -->
                        <el-button type="primary" @click="cancelUpload">取消</el-button>
                        <!-- 确定上传：绑定determineUpload方法 -->
                        <el-button type="primary" @click="determineUpload($event)">确定</el-button>
                        <!-- 图片旋转：绑定rotateRight方法 -->
                        <el-button type="primary" @click="rotateRight()">向右边旋转90度</el-button>
                    </el-col>
                </el-row>
            </div>
        </div>
    </div>

    <div style="height:380px;margin-top: 20px;">
        <el-space :size="30">
            <label class="btn-photo" for="upload2">选择文章标题图片(可以不设置，使用默认值)</label>
            <input ref="upload" type="file" id="upload2" style="position:absolute; clip:rect(0 0 0 0);"
                accept="image/png, image/jpeg, image/gif, image/jpg" @change="uploadImg($event)">
            <el-button type="primary" @click="freshCropper">清空图片</el-button>
        </el-space>
        <!-- 如果是编辑状态，将thumbnail图片显示在一个img中，添加状态则隐藏 -->
        <div v-if="data.isEditor" style="margin-top:10px">
            <img :src="data.thumbnail" />
        </div>
        <!--显示下列div的时机：1、添加文章；
2、编辑文章时剪切的图片上传到服务器后，要在本地显示图片时 -->
        <div v-if="!data.isEditor" ref="cTextareaRef" contenteditable="true" class="textdiv" id="text"
            @click="getLastEditRange()" @keyup="getLastEditRange()" v-html="data.innerHTML" @blur="onBlur($event)">
        </div>
    </div>
</template>

<style scoped>
.cropper-control {
    margin-top: 10px;
    /* margin-bottom:5px; */
    text-align: right;
}

.card {
    margin: 1% 1%;
}

@media (min-width: 480px) {
    .textdiv {
        border: 1px solid #ccc;
        margin-bottom: 20px;
        margin-top: 10px;
        padding: 5px 10px;
        min-width: 10%;
        max-width: 70%;
    }
}

.btn-photo {
    height: 25px;
    width: 350px;
    text-align: center;
    padding: 5px;
    border-radius: 4px;
    color: #fff;
    background-color: #50bfff;
    white-space: nowrap;
    /* 确保文字不换行 */
    overflow: hidden;
    /* 隐藏溢出内容 */
    text-overflow: ellipsis;
    /* 文字溢出时显示省略号 */
}

.white_content {
    left: 16%;
    width: 70%;
    height: 70%;
}

@media (max-width: 480px) {
    .textdiv {
        border: 1px solid #ccc;
        margin-bottom: 20px;
        margin-right: 3%;
        padding: 1%;
        width: 74%;
    }
}

.btn-photo {
    height: 23px;
    width: 100%;
    /* 改为100%宽度，占据父容器全部宽度 */
    min-width: 200px;
    /* 设置最小宽度，确保能显示完整文字 */
    font-size: 12px;
    padding: 5px;
    /* 使用固定像素值代替百分比 */
    border-radius: 4px;
    color: #fff;
    background-color: #50bfff;
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
}

.white_content {
    left: 5%;
    width: 90%;
    height: 80%;
}

.card {
    margin: 1% 1%;
}

.textdiv img {
    max-width: 100%;
    display: block;
}

.black_overlay {
    display: none;
    position: absolute;
    top: 0%;
    left: 0%;
    width: 100%;
    height: 100%;
    background-color: black;
    z-index: 1000;
    filter: alpha(opacity=88);
}

.white_content {
    position: absolute;
    top: 16%;
    padding: 1%;
    border: 5px solid orange;
    background-color: white;
    z-index: 1002;
    overflow: auto;
}

.btn {
    margin-top: 15px;
    margin-left: 20px;
    float: right;
}

.test {
    height: 92%;
}
</style>