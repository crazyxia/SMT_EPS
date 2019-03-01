import Vue from 'vue'
import Vuex from 'vuex'
import * as mutations from './mutations'
import * as actions from './actions'
import * as getters from './getters'

Vue.use(Vuex);

const state = {
  //登录标识
  token:'',
  //线号数组
  lines:[],
  //线数
  lineSize:0,
  //加载组件
  loading:false,
  denied:false,

  /*user*/
  user:{},
  loginUser:{},
  userPage:-1,
  userTypes:['仓库管理员','厂线操作员','IPQC','超级管理员','生产管理员','品质管理员','工程管理员','仓库管理员'],

  /*program*/
  program:{},
  //站位表详情查询条件
  programPage:-1,
  //站位表详情列表
  programItemList:[],
  programInfo:{},
  //操作对象数组
  operations:[],

  /*material*/
  material:{},
  materialPage:-1,

  //当前操作的客户报表对象
  client:{},
  clientPage:-1,

  //当前操作的操作报表对象
  pageInfo:{},
  operation:{},
  //操作详情列表查询条件
  detail:{},
  operationPage:-1,
  operationDetailPage:-1,

  /*io*/
  io:{},
  ioPage:-1,

  /*display*/
  lineStatusData:[],
  lineAllDayData:{},

};

const store = new Vuex.Store({
  state,
  getters,
  actions,
  mutations
});

export default store;
