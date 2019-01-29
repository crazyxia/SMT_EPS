export const setToken = (state,token) =>{
  state.token = token;
};

export const setLoading = (state,loading) =>{
  state.loading = loading;
};

export  const setLines = (state,lines) =>{
  state.lines = lines;
};

export  const setLineSize = (state,lineSize) =>{
  state.lineSize = lineSize;
};

export  const setDenied = (state,denied) =>{
  state.denied = denied;
};
/*user*/
export  const setUser= (state,user) =>{
  state.user = user;
};
export  const setLoginUser= (state,loginUser) =>{
  state.loginUser = loginUser;
};
export  const setUserPage= (state,userPage) =>{
  state.userPage = userPage;
};
/*program*/
export  const setProgram = (state,program) =>{
  state.program = program;
};
export  const setProgramInfo = (state,programInfo) =>{
  state.programInfo = programInfo;
};
export  const setProgramPage = (state,programPage) =>{
  state.programPage = programPage;
};
export  const setProgramItemList = (state,programItemList) =>{
  state.programItemList = programItemList;
};
export  const setOperations = (state,operations) =>{
  state.operations = operations;
};
/*material*/
export  const setMaterialPage = (state,materialPage) =>{
  state.materialPage = materialPage;
};
export  const setMaterial = (state,material) =>{
  state.material = material;
};

/*client*/
export  const setClient = (state,client) =>{
  state.client = client;
};
export  const setClientPage = (state,clientPage) =>{
  state.clientPage = clientPage;
};

/*operation*/
export  const setOperation = (state,operation) =>{
  state.operation = operation;
};
export  const setDetail = (state,detail) =>{
  state.detail = detail;
};
export  const setOperationPage = (state,operationPage) =>{
  state.operationPage = operationPage;
};
export  const setOperationDetailPage = (state,operationDetailPage) =>{
  state.operationDetailPage = operationDetailPage;
};
/*io*/
export  const setIOPage = (state,ioPage) =>{
  state.ioPage = ioPage;
};
export  const setIO = (state,io) =>{
  state.io = io;
};

/*display*/
export  const setLineData =(state, lineStatusData) => {
  state.lineStatusData = lineStatusData.data
};

export  const setAllDayData = (state, lineAllDayData) => {
  state.lineAllDayData = lineAllDayData.data
};
