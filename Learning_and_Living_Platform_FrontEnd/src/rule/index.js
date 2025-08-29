
const notNull = (rule, value, callback) => {
    if (value === '') {
        callback(new Error());
    } else {
        callback();
    }
}


const ruleValidate = {
    name: [
        { validator: notNull, message: '昵称不能为空', trigger: 'blur' }
    ],
    email: [
        { validator: notNull, message: '邮箱不能为空', trigger: 'blur' },
        { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }
    ],
    gender: [
        { validator: notNull, message: '请选择您的性别', trigger: 'change' }
    ],
    birthday: [
        { validator: notNull, type: 'date', message: '请选择您的生日', trigger: 'change' }
    ],
    newPassword: [
        { validator: notNull, message: '请输入您的密码', trigger: 'blur' },
        { type: 'string', min: 6, message: '密码不能短于6位', trigger: 'blur' },
        { type: 'string', max: 16, message: '密码不能长于16位', trigger: 'blur' }
    ]
}



export { ruleValidate }