export const setToken = ({commit},token) =>{
  commit('setToken',token)
};

export const setLoading = ({commit},loading) =>{
  commit('setLoading',loading)
};

export  const setLines = ({commit},lines) =>{
  commit('setLines',lines)
};

export  const setLineSize = ({commit},lineSize) =>{
  commit('setLineSize',lineSize)
};

export  const setDenied = ({commit},denied) =>{
  commit('setDenied',denied)
};
/*user*/
export  const setUser= ({commit},user) =>{
  commit('setUser',user)
};
export  const setLoginUser= ({commit},loginUser) =>{
  commit('setLoginUser',loginUser)
};
export  const setUserPage= ({commit},userPage) =>{
  commit('setUserPage',userPage)
};
/*program*/
export  const setProgram = ({commit},program) =>{
  commit('setProgram',program)
};
export  const setProgramInfo= ({commit},programInfo) =>{
  commit('setProgramInfo',programInfo)
};
export  const setProgramPage = ({commit},programPage) =>{
  commit('setProgramPage',programPage)
};
export  const setProgramItemList = ({commit},programItemList) =>{
  commit('setProgramItemList',programItemList)
};
export  const setOperations = ({commit},operations) =>{
  commit('setOperations',operations)
};
/*material*/
export  const setMaterialPage = ({commit},materialPage) =>{
  commit('setMaterialPage',materialPage)
};
export  const setMaterial = ({commit},material) =>{
  commit('setMaterial',material)
};
/*client*/
export  const setClient = ({commit},client) =>{
  commit('setClient',client)
};
export  const setClientPage = ({commit},clientPage) =>{
  commit('setClientPage',clientPage)
};
/*operation*/
export  const setOperation = ({commit},operation) =>{
  commit('setOperation',operation)
};
export  const setDetail = ({commit},detail) =>{
  commit('setDetail',detail)
};
export  const setOperationPage = ({commit},operationPage) =>{
  commit('setOperationPage',operationPage)
};
export  const setOperationDetailPage = ({commit},operationDetailPage) =>{
  commit('setOperationDetailPage',operationDetailPage)
};
/*io*/
export  const setIOPage = ({commit},ioPage) =>{
  commit('setIOPage',ioPage)
};
export  const setIO = ({commit},io) =>{
  commit('setIO',io)
};
/*display*/
export  const setLineData =({commit}, lineStatusData) => {
  commit('setLineData',lineStatusData)
};

export  const setAllDayData = ({commit}, lineAllDayData) => {
  commit('setAllDayData',lineAllDayData)
};
