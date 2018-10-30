import Vue from 'vue'
import Vuex from 'vuex'

Vue.use(Vuex)

export default new Vuex.Store({
  state: {
    token:'',
    lines:[],
    lineSize:0,
    loading:false,
    denied:false,

    /*操作按钮*/
    isAdd:false ,//是否点击添加
    isFind:false,//是否点击查询
    isRefresh:false,//是否刷新列表
    isUpdate:false, //是否点击修改
    isDelete:false, //是否点击删除
    isGetCodePic:false, //是否点击生成工号二维码
    isUploadFinish:false, //文件是否上传成功
    isDetail:false,


    /*user*/
    userList:[],
    userOperationType:"",
    user:{},   //正在操作的user对象
    userId:"", //需要生成二维码的工号

    /*program*/
    programList:[],
    programOperationType:"",
    program:{},
    oldState:"",
    programItemShow:false,
    programItemList:[],
    programItemOperationType:"",
    programItem:{},
    operationIndex:"",
    programItemRefresh:false,

    /*material*/
    materialList:[],
    materialOperationType:"",
    material:{},

    /*client*/
    clientList:[],
    client:{},

    /*opration*/
    operationList:[],
    operationSummaryList:[],
    operationSummary:{},
    operationDetailShow:false,

    /*io*/
    ioList:[],

    /*display*/
    lineStatusData:[],
    lineAllDayData:{},
  },
  mutations: {
    setToken(state,token){
      state.token = token; 
    },
    setLoading(state,loading){
      state.loading = loading;
    },
    setLines(state,lines){
      state.lines = lines;
    },
    setLineSize(state,lineSize){
      state.lineSize = lineSize;
    },
    setDenied(state,denied){
      state.denied = denied;
    },

    /*operation*/
    setIsAdd(state,isAdd){
      state.isAdd = isAdd;
    },
    setIsUpdate(state,isUpdate){
      state.isUpdate = isUpdate;
    },
    setIsRefresh(state,isRefresh){
      state.isRefresh = isRefresh;
    },
    setIsFind(state,isFind){
      state.isFind = isFind;
    },
    setIsDelete(state,isDelete){
      state.isDelete = isDelete;
    },
    setIsGetCodePic(state,isGetCodePic){
      state.isGetCodePic = isGetCodePic;
    },
    setIsUploadFinish(state,isUploadFinish){
      state.isUploadFinish = isUploadFinish;
    },
    setIsDetail(state,isDetail){
      state.isDetail = isDetail;
    },

    /*user*/
    setUserId(state,userId){
      state.userId = userId;
    },
    setUserList(state,userList){
      state.userList = userList;
    },
    setUser(state,user){
      state.user = user;
    },
    setUserOperationType(state,userOperationType){
      state.userOperationType = userOperationType;
    },

    /*program*/
    setProgramList(state,programList){
      state.programList = programList;
    },
    setProgram(state,program){
      state.program = program;
    },
    setProgramOperationType(state,programOperationType){
      state.programOperationType = programOperationType;
    },
    setOldState(state,oldState){
      state.oldState = oldState;
    },
    setProgramState(state,programState){
      state.program.state = programState;
    },
    setProgramItemShow(state,programItemShow){
      state.programItemShow = programItemShow;
    },
    setProgramItemList(state,programItemList){
      state.programItemList = programItemList;
    },
    setProgramItem(state,programItem){
      state.programItem = programItem;
    },
    setProgramItemOperationType(state,programItemOperationType){
      state.programItemOperationType = programItemOperationType;
    },
    setOperationIndex(state,operationIndex){
      state.operationIndex = operationIndex;
    },
    setProgramItemRefresh(state,programItemRefresh){
      state.programItemRefresh = programItemRefresh;
    },

    /*material*/
    setMaterialOperationType(state,materialOperationType){
      state.materialOperationType = materialOperationType;
    },
    setMaterialList(state,materialList){
      state.materialList = materialList;
    },
    setMaterial(state,material){
      state.material = material;
    },

    /*client*/
    setClientList(state,clientList){
      state.clientList = clientList;
    },
    setClient(state,client){
      state.client = client;
    },

    /*operation*/
    setOperationList(state,operationList){
      state.operationList = operationList;
    },
    setOperationSummary(state,operationSummary){
      state.operationSummary = operationSummary;
    },
    setOperationSummaryList(state,operationSummaryList){
      state.operationSummaryList = operationSummaryList;
    },
    setOperationDetailShow(state,operationDetailShow){
      state.operationDetailShow = operationDetailShow;
    },

    /*io*/
    setIoList(state,ioList){
      state.ioList = ioList;
    },

    /*display*/
    setLineData(state, data) {
      state.lineStatusData = data.data
    },
    setAllDayData(state, data) {
      state.lineAllDayData = data.data
    }
  },
  actions: {
  }
})
