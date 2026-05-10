declare namespace API {
  type adminDeleteParams = {
    deleteRequest: DeleteRequest
  }

  type adminGetByIdParams = {
    id: string
  }

  type AppAddDTO = {
    initPrompt?: string
  }

  type AppAdminUpdateDTO = {
    id?: string
    appName?: string
    cover?: string
    priority?: number
  }

  type AppDeployDTO = {
    appId?: string
  }

  type AppQueryDTO = {
    pageNum?: number
    paseSize?: number
    sortField?: string
    sortOrder?: string
    id?: string
    appName?: string
    cover?: string
    initPrompt?: string
    codeGenType?: string
    deployKey?: string
    priority?: number
    userId?: string
  }

  type AppUpdateDTO = {
    id?: string
    appName?: string
  }

  type AppVO = {
    id?: string
    appName?: string
    cover?: string
    initPrompt?: string
    codeGenType?: string
    deployKey?: string
    deployedTime?: string
    priority?: number
    userId?: string
    createTime?: string
    updateTime?: string
    user?: UserVO
  }

  type BaseResponseAppVO = {
    code?: number
    data?: AppVO
    message?: string
  }

  type BaseResponseBoolean = {
    code?: number
    data?: boolean
    message?: string
  }

  type BaseResponseLong = {
    code?: number
    data?: string
    message?: string
  }

  type BaseResponsePageAppVO = {
    code?: number
    data?: PageAppVO
    message?: string
  }

  type BaseResponsePageChatHistory = {
    code?: number
    data?: PageChatHistory
    message?: string
  }

  type BaseResponsePageUserVO = {
    code?: number
    data?: PageUserVO
    message?: string
  }

  type BaseResponseString = {
    code?: number
    data?: string
    message?: string
  }

  type BaseResponseUser = {
    code?: number
    data?: User
    message?: string
  }

  type BaseResponseUserVO = {
    code?: number
    data?: UserVO
    message?: string
  }

  type ChatHistory = {
    id?: string
    message?: string
    appId?: string
    userId?: string
    messageType?: number
    createTime?: string
    updateTime?: string
    isDelete?: number
  }

  type ChatHistoryQueryDTO = {
    pageNum?: number
    pageSize?: number
    sortField?: string
    sortOrder?: string
    id?: string
    message?: string
    messageType?: number
    appId?: string
    userId?: string
    lastCreateTime?: string
  }

  type chatToGenCodeParams = {
    appId: string
    message: string
  }

  type deleteOneselfParams = {
    deleteRequest: DeleteRequest
  }

  type DeleteRequest = {
    id?: string
  }

  type deleteUserParams = {
    deleteRequest: DeleteRequest
  }

  type getAppVOByIdParams = {
    id: string
  }

  type getUserByIdParams = {
    id: string
  }

  type getUserVOByIdParams = {
    id: string
  }

  type listAppChatHistoryParams = {
    appId: string
    pageSize?: number
    lastCreateTime?: string
  }

  type PageAppVO = {
    records?: AppVO[]
    pageNumber?: number
    pageSize?: number
    totalPage?: number
    totalRow?: number
    optimizeCountQuery?: boolean
  }

  type PageChatHistory = {
    records?: ChatHistory[]
    pageNumber?: number
    pageSize?: number
    totalPage?: number
    totalRow?: number
    optimizeCountQuery?: boolean
  }

  type PageUserVO = {
    records?: UserVO[]
    pageNumber?: number
    pageSize?: number
    totalPage?: number
    totalRow?: number
    optimizeCountQuery?: boolean
  }

  type ServerSentEventString = true

  type serveStaticResourceParams = {
    deploy: string
  }

  type User = {
    id?: string
    userAccount?: string
    userPassword?: string
    userName?: string
    userAvatar?: string
    userProfile?: string
    userRole?: number
    editTime?: string
    createTime?: string
    updateTime?: string
    isDelete?: number
  }

  type UserAddDTO = {
    userName?: string
    userAccount?: string
    userAvatar?: string
    userProfile?: string
    userRole?: number
  }

  type UserLoginDTO = {
    userAccount?: string
    userPassword?: string
  }

  type UserQueryDTO = {
    pageNum?: number
    paseSize?: number
    sortField?: string
    sortOrder?: string
    id?: string
    userName?: string
    userAccount?: string
    userProfile?: string
    userRole?: number
    startTime?: string
    endTime?: string
  }

  type UserRegisterDTO = {
    userAccount?: string
    userPassword?: string
    checkPassword?: string
  }

  type UserUpdateDTO = {
    id?: string
    userAccount?: string
    userPassword?: string
    userName?: string
    userAvatar?: string
    userProfile?: string
    userRole?: number
  }

  type UserVO = {
    id?: string
    userAccount?: string
    userName?: string
    userAvatar?: string
    userProfile?: string
    userRole?: number
    createTime?: string
    updateTime?: string
  }
}
