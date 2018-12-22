<template>
  <div class="leftNav">
    <ul class="lists">
      <li v-for="(list,index) in lists" :key="list.text" @mouseover="overShow(index)" @mouseout="outHide(index)" :class="listStates.isActive[index]?'active':''"  class="list">
        <div @click="activeChange(index)">
          <icon :name="list.logoSrc[listStates.logoImg[index]]" :scale="imgScale"></icon>
          <span>{{list.text}}</span>
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
      imgScale:4,
      lists:[
        {
          id:0,
          logoSrc:['manage','manageB'], 
          text:'管理',
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
          text:'实时监控'
        }
      ],
      listStates:{
        state:[0,0,0],                  // 0表示无状态，1表示hover，2表示click
        logoImg:[0,0,0],                // 0表示logoSrc第一个图片
        isActive:[false,false,false],   // 点击后的class状态
        childActive:[[false,false,false,false],[false,false,false],[false,false]]
      }
    }
  },
  methods:{
    overShow:function(index){
        var state = this.listStates.state[index];
        if(state == 0){
          this.$set(this.listStates.state,index,1);
          this.$set(this.listStates.logoImg,index,1);
        }
    },
    outHide:function(index){
        var state = this.listStates.state[index];
        if(state == 1){
          this.$set(this.listStates.state,index,0);
          this.$set(this.listStates.logoImg,index,0);
        }
    },
    activeChange:function(index){
      if(index != 2){
        var length = this.listStates.state.length;
        for(var i = 0;i < length; i++){
          if(i!=index){
            this.$set(this.listStates.state,i,0);
            this.$set(this.listStates.logoImg,i,0);
            this.$set(this.listStates.isActive,i,false);
          }
        }
        var state = this.listStates.state[index];
        if(state != 2){
          this.$set(this.listStates.state,index,2);
          this.$set(this.listStates.logoImg,index,1);
          this.$set(this.listStates.isActive,index,true);
        }else{
          this.$set(this.listStates.state,index,0);
          this.$set(this.listStates.logoImg,index,0);
          this.$set(this.listStates.isActive,index,false);
        }
      }else{
        this.$router.push('display');
      }
    },
    sendItemMessage:function(id,index){
      var childActive = this.listStates.childActive;
      for(var i = 0;i<childActive.length;i++){
        var item = childActive[i];
        for(var j = 0;j<item.length;j++){
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
    width:70px;
    min-height:100%;
    border-right:1px solid #ccc;
    ul.lists{
      margin-top:60px;
      width:100%;
      li.list{
        cursor:pointer;
        width:100%;
        margin:20px 0;
        padding:10px 0;
        div{
          width:100%;
          height:60px;
          display:flex;
          flex-direction:column;
          flex-wrap:nowrap;
          align-items:center;
          img{
            width:32px;
            height:32px;
          }
          span{
            font-size:16px;
          }
        }
        ul{
          display:none;
          position:absolute;
          left:70px;
          width:180px;
          text-align:left;
          margin-top:-60px;
          border:1px solid #ccc;
          border-left:none;
          padding:10px 0;
          z-index:999;
          background:#fff;
          li{
            cursor:pointer;
            width:100%;
            height:40px;
            line-height:40px;
            padding-left:10px;
          }
          li:hover,li.clickActive{
            background:rgba(245,245,245,0.6);
            color:#188AE2;
          }
        }
      }
      li:hover,li.active{
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
