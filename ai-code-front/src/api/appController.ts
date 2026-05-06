// @ts-ignore
/* eslint-disable */
import request from '@/request'

/** 此处后端没有提供注释 POST /app/add */
export async function addApp(body: API.AppAddDTO, options?: { [key: string]: any }) {
  return request<API.BaseResponseLong>('/app/add', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  })
}

/** 此处后端没有提供注释 DELETE /app/admin/delete */
export async function adminDelete(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.adminDeleteParams,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseBoolean>('/app/admin/delete', {
    method: 'DELETE',
    params: {
      ...params,
      deleteRequest: undefined,
      ...params['deleteRequest'],
    },
    ...(options || {}),
  })
}

/** 此处后端没有提供注释 GET /app/admin/get */
export async function adminGetById(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.adminGetByIdParams,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseAppVO>('/app/admin/get', {
    method: 'GET',
    params: {
      ...params,
    },
    ...(options || {}),
  })
}

/** 此处后端没有提供注释 POST /app/admin/list/page/vo */
export async function adminListByPage(body: API.AppQueryDTO, options?: { [key: string]: any }) {
  return request<API.BaseResponsePageAppVO>('/app/admin/list/page/vo', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  })
}

/** 此处后端没有提供注释 PUT /app/admin/update */
export async function adminUpdate(body: API.AppAdminUpdateDTO, options?: { [key: string]: any }) {
  return request<API.BaseResponseBoolean>('/app/admin/update', {
    method: 'PUT',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  })
}

/** 此处后端没有提供注释 GET /app/chat/gen/code */
export async function chatToGenCode(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.chatToGenCodeParams,
  options?: { [key: string]: any }
) {
  return request<API.ServerSentEventString[]>('/app/chat/gen/code', {
    method: 'GET',
    params: {
      ...params,
    },
    ...(options || {}),
  })
}

/** 此处后端没有提供注释 POST /app/delete */
export async function deleteOneself(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.deleteOneselfParams,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseBoolean>('/app/delete', {
    method: 'POST',
    params: {
      ...params,
      deleteRequest: undefined,
      ...params['deleteRequest'],
    },
    ...(options || {}),
  })
}

/** 此处后端没有提供注释 POST /app/deploy */
export async function deployApp(body: API.AppDeployDTO, options?: { [key: string]: any }) {
  return request<API.BaseResponseString>('/app/deploy', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  })
}

/** 此处后端没有提供注释 GET /app/get */
export async function getAppVoById(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.getAppVOByIdParams,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseAppVO>('/app/get', {
    method: 'GET',
    params: {
      ...params,
    },
    ...(options || {}),
  })
}
// 别名，兼容大小写
export const getAppVOById = getAppVoById

/** 此处后端没有提供注释 POST /app/list/page/featured */
export async function listFeaturedByPage(body: API.AppQueryDTO, options?: { [key: string]: any }) {
  return request<API.BaseResponsePageAppVO>('/app/list/page/featured', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  })
}

/** 此处后端没有提供注释 POST /app/list/page/vo */
export async function listOneselfByPage(body: API.AppQueryDTO, options?: { [key: string]: any }) {
  return request<API.BaseResponsePageAppVO>('/app/list/page/vo', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  })
}

/** 此处后端没有提供注释 PUT /app/update/oneself */
export async function updateOneself(body: API.AppUpdateDTO, options?: { [key: string]: any }) {
  return request<API.BaseResponseBoolean>('/app/update/oneself', {
    method: 'PUT',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  })
}
