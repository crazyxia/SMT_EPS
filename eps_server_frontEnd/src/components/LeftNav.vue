<template>
  <div class="leftNav">
    <img src="../../static/img/logo.svg"/>
    <ul class="lists">
      <li v-for="(list,index) in lists" :key="list.id" @mouseover="overShow(index)" @mouseout="outHide(index)" :class="listStates.isActive[index]?'active':''" class="list">
        <div @click="activeChange(index)">
           <p><icon :name="list.logoSrc[listStates.logoImg[index]]" :scale="iconScale"></icon><span>{{list.text}}</span></p>
           <icon :name="list.arrowImgSrc[listStates.arrowImg[index]]" class="arrow" :scale="2" v-if="list.id !== 2"></icon>
        </div>
        <ul>
          <li v-for="(item,index) in list.childList" :key="item.name" @click="sendItemMessage(list.id,index)" :class="listStates.childActive[list.id][index]?'clickActive':''">
            {{item.name}}
          </li>
        </ul>
      </li> 
    </ul>
  </div>
</template>

<script>
export default {
  name: 'leftNav',
  data () {
    return {
      iconScale:3,
      lists:[
        {
          id:0,
          logoSrc:['manage','manageB'], 
          text:'管理',
          arrowImgSrc:['right','rightB','down'],
          childList:[
            {
              name:'人员管理',
              component:'User',
            },{
              name:'站位表管理',
              component:'Program',
            },{
              name:'报警配置管理',
              component:'Config',
            },{
              name:'物料保质期管理',
              component:'Materials',
            }
          ]
        },
        { 
          id:1,
          logoSrc:['reportForm','reportFormB'], 
          text:'报表',
          arrowImgSrc:['right','rightB','down'],
          childList:[
            {
              name:'客户报表',
              component:'ClientReport',
            },{
              name:'操作报表',
              component:'OperationReport',
            },{
              name:'仓库出入库报表',
              component:'IOReport',
            }
          ]
        },
        {
          id:2,
          logoSrc:['display','displayB'], 
          text:'实时监控',
          arrowImgSrc:['right','rightB','down'],
        }
      ],
      listStates:{
        state:[0,0,0],                  // 0表示无状态，1表示hover，2表示click
        logoImg:[0,0,0],                // 0表示logoSrc第一个图片
        arrowImg:[0,0,0],               // 0表示arrowImgSrc第一个图片  
        isActive:[false,false,false],   // 点击后的class状态
        childActive:[[false,false,false,false],[false,false,false],[false,false]]
      }
    }
  },
  methods:{
    overShow:function(index){
        let state = this.listStates.state[index];
        if(state === 0){
          this.$set(this.listStates.state,index,1);
          this.$set(this.listStates.logoImg,index,1);
          this.$set(this.listStates.arrowImg,index,1);
        }
    },
    outHide:function(index){
        let state = this.listStates.state[index];
        if(state === 1){
          this.$set(this.listStates.state,index,0);
          this.$set(this.listStates.logoImg,index,0);
          this.$set(this.listStates.arrowImg,index,0);
        }
    },
    activeChange:function(index){
      if(index !== 2){
        let state = this.listStates.state[index];
        if(state !== 2){
          this.$set(this.listStates.state,index,2);
          this.$set(this.listStates.logoImg,index,1);
          this.$set(this.listStates.arrowImg,index,2);
          this.$set(this.listStates.isActive,index,true);
        }else{
          this.$set(this.listStates.state,index,1);
          this.$set(this.listStates.logoImg,index,1);
          this.$set(this.listStates.arrowImg,index,1);
          this.$set(this.listStates.isActive,index,false);
        }
      }else{
        this.$router.push('display');
      }
    },
    sendItemMessage:function(id,index){
      let childActive = this.listStates.childActive;
      for(let i = 0;i<childActive.length;i++){
        let item = childActive[i];
        for(let j = 0;j<item.length;j++){
          this.$set(item,j,false);
        }
      }
      this.$set(childActive[id],index,true);
      this.$emit("getNavInfo",this.lists[id].childList[index]);
    }
  }
}
</script>

<style scoped lang="scss">
  .leftNav{
    padding:0;
    width:100%;
    min-height:100%;
    img{
      display:inline-block;
      box-sizing:border-box;
      width:100%;
      padding:10px 10px;
    }
    ul.lists{
      width:100%;
      padding-bottom:20px;
      color:#333;
      li.list{
        cursor:pointer;
        width:100%;
        div{
          width:100%;
          height:60px;
          display:flex;
          flex-wrap:nowrap;
          justify-content:space-between;
          align-items:center;
          p{
            padding-left:15px;
            display:flex;
            flex-wrap:nowrap;
            justify-content:space-between;
            align-items:center;
            margin:0 !important;
            span{
              padding-left:10px;
              font-size:18px;
            }
          }
          svg.arrow{
            margin-top:-1px;
            margin-right:10px;
          }
        }
        ul{
          display:none;
          width:100%;
          li{
            box-sizing:border-box;
            width:100%;
            height:40px;
            line-height:40px;
            padding-left:54px;
          }
          li:hover,li.clickActive{
            color:#188AE2;
            background:rgba(211,211,211,0.3);
          }        
        }
      }
      li:hover,li.active{
        background:rgba(211,211,211,0.2);
        span{
          color:#188AE2;
        }
      }
      li.active{
        ul{
          display:block;
        }
      }
    }
  }
</style>
