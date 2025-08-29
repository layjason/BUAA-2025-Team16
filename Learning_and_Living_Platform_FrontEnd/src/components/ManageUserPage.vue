<template>
    <div>
    <div class="manage-container">
        <div class="manage-left">
        </div>
        <div class="manage-centre">
            <v-card style="margin-top: 30px;min-height: 800px;"
            >
                <template v-slot:title>
                用户管理
                <Button
                 style="margin-left: 13%;" 
                 type="primary"
                 @click="()=>{addUserModal = !addUserModal}"
                 >
                 <p style="color: white;">添加新用户</p>
                </Button>
                </template>

                <v-card-text style="margin-top: 20px;">
                    <Table
                    :columns="columns" :data="data">
                        <template #uid="{ row }">
                        <span>{{ row.id }}</span>
                        </template>

                        <template #name="{ row }">
                        <span>{{ row.name }}</span>
                        </template>

                        <template #action="{ row, index }">
                        <div>
                            <Row :gutter="8" justify="center">
                                <Col>
                                    <Button size="small" @click="handleEdit(row, index)">修改信息</Button>
                                </Col>
                                <Col>
                                    <Button size="small" @click="handlePass(row)">修改密码</Button>
                                </Col>
                                <Col>
                                    <Button size="small" type="error" @click="handleDelete(row)">
                                        <p style="color: white;">删除账号</p>
                                    </Button>
                                </Col>
                            </Row>
                        </div>
                        </template>
                    </Table>
                    <Spin size="large" fix :show="isLoading"></Spin>
                </v-card-text>
                <footer>
                    <Page style="text-align: center" :page-size="pageSize"
                :total="num" v-model:model-value="current" size="small" show-elevator show-total @on-change="getlist()" />
                </footer>
            </v-card>
            <Modal
                v-model="editModal"
                title="编辑用户信息"
                width="800">
                <Form ref="editref" :model="editForm" :label-width="80" :rules="ruleValidate">
                    <FormItem label="昵称" prop="name">
                        <Input v-model="editForm.name"></Input>
                    </FormItem>
                    <FormItem label="性别" prop="gender">
                        <RadioGroup v-model="editForm.gender">
                            <Radio label="Male">男</Radio>
                            <Radio label="Female">女</Radio>
                            <Radio label="Unknown">未设置</Radio>
                        </RadioGroup>
                    </FormItem>
                    <FormItem label="生日" prop="birthday">
                        <DatePicker v-model="editForm.birthday" style="width: 200px" />
                    </FormItem>
                    <FormItem label="邮箱" prop="email">
                        <Input v-model="editForm.email"></Input>
                    </FormItem>
                </Form>
                <template #footer>
                    <Button type="primary" @click="confirmEdit" ><p style="color: white;">提交</p></Button>
                    <Button @click="cancelEdit" style="margin-left: 10px;">取消</Button>
                </template>
            </Modal>
            <Modal
                v-model="editPassModal"
                title="修改用户密码"
                width="800">
                <Form ref="editPassref" :model="editPassForm" :label-width="80" :rules="ruleValidate">
                    <FormItem label="新密码" prop="newPassword">
                        <Input v-model="editPassForm.newPassword"></Input>
                    </FormItem>
                </Form>
                <template #footer>
                    <Button type="primary" @click="ConfirmEditPass" ><p style="color: white;">提交</p></Button>
                    <Button @click="cancelEditPass" style="margin-left: 10px;">取消</Button>
                </template>
            </Modal>
            <Modal
                v-model="deleteModal"
                title="您确定要删除这个账号?">
                <p>注意: 账号一旦被删除将不能恢复</p>
                <template #footer>
                    <Button type="error" @click="deleteOk" ><p style="color: white;">删除</p></Button>
                    <Button @click="deleteCancel" style="margin-left: 10px;">取消</Button>
                </template>
            </Modal>
            <Modal
                v-model="addUserModal"
                title="添加新用户"
                width="800">
                <Form :model="registerForm" :label-width="80">
                    <FormItem label="邮箱">
                        <Input v-model="registerForm.email"></Input>
                    </FormItem>
                    <FormItem label="密码">
                        <Input v-model="registerForm.password"></Input>
                    </FormItem>
                    <FormItem label="确认密码">
                        <Input v-model="registerForm.password_repeat"></Input>
                    </FormItem>
                </Form>
                <template #footer>
                    <Button type="primary" @click="register" ><p style="color: white;">添加</p></Button>
                    <Button @click="cancelAdd" style="margin-left: 10px;">取消</Button>
                </template>
            </Modal>
        </div>
        <div class="manage-right">
        
        </div>
    </div>
    </div>
</template>
    

<script setup>
import { resolveComponent, ref, reactive } from 'vue';
import { post, put, delet } from "@/net";
import { Snackbar } from '@varlet/ui'
import { ruleValidate } from "@/rule"
import { sha256 } from 'js-sha256';
const num = ref(30)
const current = ref(1)
const pageSize = 13
const editModal = ref(false)
const editPassModal = ref(false)
const deleteModal= ref(false)
const deleteId = ref(-1)
const isLoading = ref(false)

const data = ref([])
const editIndex =  ref(-1)  // 当前聚焦的输入框的行数

    
const columns = reactive([
{
    type: 'expand',
    width: 30,
        render: (h, { row: { gender, birthday, email, registerTime, logInNum, userLevel, exp } }) => {
            return h(resolveComponent('DescriptionList'), {
                title: "详细信息",
                layout: "vertical",
                col: 3
            }, () => [
                h(resolveComponent('Description'), {
                    term: "性别:"
                }, () => gender),
                h(resolveComponent('Description'), {
                    term: "生日:"
                }, () => {if(birthday){return birthday}else{return '暂无'}}),
                h(resolveComponent('Description'), {
                    term: "邮箱:"
                }, () => email),
                h(resolveComponent('Description'), {
                    term: "注册时间:"
                }, () => registerTime),
                h(resolveComponent('Description'), {
                    term: "登陆天数:"
                }, () => logInNum),
                h(resolveComponent('Description'), {
                    term: "等级:"
                }, () => (userLevel + ' (exp: ' + exp + ')') ),
            ]
            );
        }
    },
    {
        title: 'uid',
        slot: 'uid',
        width: 150
    },

    {
        title: '昵称',
        slot: 'name',
        width: 200
    },
    {
        title: '操作',
        slot: 'action',
        align: 'center'
    }
])

const editForm = reactive({
    id: '',
    name: '',
    gender: '',
    email: '',
    birthday: '',
    profilePhotoUrl: ''
})

const editPassForm = reactive({
    userId: '',
    newPassword:''
})


const editref = ref(null)
const editPassref = ref(null)

function handleEdit(row, index) {
    editModal.value = true;
    editIndex.value = index;
    editForm.name = row.name;
    editForm.gender = row.gender;
    editForm.email = row.email;
    editForm.id = row.id;
    editForm.profilePhotoUrl = row.profilePhotoUrl;
    editForm.birthday = row.birthday;
}

function confirmEdit() {
    editref.value.validate((valid) => {
        if (valid) {
            console.log(editForm);
            put('user/updateUserInfo', editForm,
                () => {
                    Snackbar.success("修改成功！")
                },
            () => { Snackbar.error("修改失败！") }
            )
            getlist()
            editIndex.value = -1;
            editModal.value = false
        } else {
            Snackbar.error("填写内容有误，修改失败！")
        }
    })
}

function cancelEdit() {
    editIndex.value = -1;
    editModal.value = false
}

function handlePass(row) {
    editPassForm.userId = row.id
    editPassModal.value = true
}

function ConfirmEditPass() {
    editPassref.value.validate((valid) => {
        if (valid) {
            post('user/getSalt', {
                idOrEmail: editPassForm.userId
            },
                (message) => {
                    put('user/updatePwd',
                        {
                            userId: editPassForm.userId,
                            newPassword: editPassForm.newPassword
                        },
                        () => { Snackbar.success("修改成功") },
                        () => { Snackbar.success("修改失败!") }
                        )
                    editPassForm.userId = ''
                    editPassForm.newPassword = ''
                    editPassModal.value = false
                },
                () => {
                    Snackbar.success("修改失败!")
                }
            )
        } else {
            Snackbar.error("您输入的密码不符合格式！")
        }
    })
}

function cancelEditPass() {
    editPassForm.userId = ''
    editPassForm.newPassword = ''
    editPassModal.value = false
}

function handleDelete(row) {
    deleteId.value = row.id;
    deleteModal.value = true;
}

function getlist() {
    isLoading.value = true
    post('user/userListByPage', {
        cntInPage: pageSize,
        pageNum: current.value
    }, (message) => {
        //console.log(message.list)
        data.value = message.list
        num.value = message.count
        isLoading.value = false
    }, () => {
        Snackbar.error("获取用户列表错误")
    })
}

function deleteOk() {
    console.log({id: deleteId.value})
    delet('user/deleteUser', {
        id: deleteId.value
    }, () => {
        getlist()
        deleteId.value = -1
        deleteModal.value = false
        Snackbar.success("删除成功")
    }, () => {
        Snackbar.error("删除失败")
    })
}
function deleteCancel () {
    deleteModal.value = false
}

//管理员添加新用户

const addUserModal = ref(false)

const registerForm = reactive({
    email: '',
    password: '',
    password_repeat: '',
    salt:''
})

function checkMail(email) {
var reg = /^[0-9a-zA-Z_.-]+[@][0-9a-zA-Z_.-]+([.][a-zA-Z]+){1,2}$/
return reg.test(email)
}

function checkRegister() {
    if (registerForm.email == '') {
        Snackbar.warning('邮箱不能为空')
        return false
    }
    if (!checkMail(registerForm.email)) {
        Snackbar.warning('请输入正确的邮箱格式')
        return false
    }
    if (registerForm.password == '') {
        Snackbar.warning('密码不能为空')
        return false
    }
    if (registerForm.password.length < 6 || registerForm.password.length>16) {
        Snackbar.warning('密码长度必须为6到16位')
        return false
    }
    if (registerForm.password != registerForm.password_repeat) {
        Snackbar.warning('两次输入的密码不一致')
        return false
    }
    return true
}

function cancelAdd() {
    addUserModal.value = false
}

function register() {
    if (checkRegister()) {
        var t = "ABCDEFGHJKMNPQRSTWXYZabcdefhijkmnprstwxyz2345678"
        var a = t.length
        var salt = ""
        for (var i = 0; i < 20; i++) salt += t.charAt(Math.floor(Math.random() * a))
        post("user/register", {
        email: registerForm.email,
        password: sha256(registerForm.password+salt),
        salt: salt
        }, 
        () => {
            Snackbar.success("添加成功!")
            addUserModal.value = false
            registerForm.email = ''
            registerForm.password = ''
            registerForm.password_repeat = ''
            },
        (mes) => { 
            Snackbar.error(mes)
        },
        )
    }
}

getlist()
      
</script>

<style>
    .manage-container {
        display: flex;
        flex-direction: row;
    }
    
    .manage-left {
        flex: 1;  /* 指定左右两栏占据相同的空间 */
        margin-left: 0px;
        margin-right: 0px;
    }
    
    .manage-right{
        flex: 1;  
        margin-left: 0px;
        margin-right: 0px;
    }
    
    .manage-centre {
        flex: 4;
        margin-top: 0px;
        margin-left: 0px;
        margin-right: 0px;
    }
    

    
</style>