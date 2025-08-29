import axios from "axios";
import { Snackbar } from '@varlet/ui'
import router from '@/router'

const defaultError = (msg, status) => {
    if (status == 401) {
        localStorage.removeItem('token')
        router.push('/login')
    }
    Snackbar.error('请求失败，错误状态码：' + status + ' 错误信息：' + msg)
}

// const defaultFailure = (msg, status) => {
//     Snackbar.warning('请求失败，错误状态码：' + status + ' 错误信息：' + msg)
// }

function post(url, data, success,  error = defaultError) {
    return axios.post(url, data,
        {
            headers: {
                'token': localStorage.getItem('token')
            }
        }
    ).then(({ data }) => {
        if(data.success)
            success(data.message, data.status)
        else
            error(data.message, data.status)
    }).catch((axiosError) => {
        if (axiosError.response) {
            error(axiosError.response.data.message, axiosError.response.status);
        } else {
            error(axiosError.message);
        }
    });
}

function get(url, success, error = defaultError) {
    return axios.get(url, 
        {
            headers:{
            'token': localStorage.getItem('token')
            }
        }).then(({ data }) => {
        if(data.success)
            success(data.message, data.status)
        else
            error(data.message, data.status)
    }).catch((axiosError) => {
        if (axiosError.response) {
            error(axiosError.response.data.message, axiosError.response.status);
        } else {
            error(axiosError.message);
        }
    });
}

function put(url, data, success, error = defaultError) {
    return axios.put(url, data,
        {
            headers: {
                'token': localStorage.getItem('token')
            }
        }
    ).then(({ data }) => {
        if(data.success)
            success(data.message, data.status)
        else
            error(data.message, data.status)
    }).catch((axiosError) => {
        if (axiosError.response) {
            error(axiosError.response.data.message, axiosError.response.status);
        } else {
            error(axiosError.message);
        }
    });
}

function delet(url, data, success, error = defaultError) {
    return axios.delete(url,
        {
            headers: {
                'token': localStorage.getItem('token')
            },
            data: data
    }).then(({ data }) => {
        if(data.success)
            success(data.message, data.status)
        else
            error(data.message, data.status)
    }).catch((axiosError) => {
        if (axiosError.response) {
            error(axiosError.response.data.message, axiosError.response.status);
        } else {
            error(axiosError.message);
        }
    });
}
export { get, post, put, delet}