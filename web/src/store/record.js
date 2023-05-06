// store用来存储用户信息，以便每次打开不同的页面都能读取用户信息
export default {
    state: {
        is_record: false,
        a_step: "",
        b_step: "",
        record_loser: "",
        record_id: null,
    },
    getters: {
    },
    mutations: { // mutations用来修改用户信息，一般放同步操作的函数
        updateIsRecord(state, is_record) {
            state.is_record = is_record;
        },
        updateSteps(state, data) {
            state.a_steps = data.a_steps;
            state.b_steps = data.b_steps;
        },
        updateRecordLoser(state, loser) {
            state.record_loser = loser;
        },
        updateRecordId(state, id) {
            state.record_id = id;
        }
    },
    actions: { //异步操作只能放到actions里，如：从服务器拉取信息            
    },
    modules: {
    }
}