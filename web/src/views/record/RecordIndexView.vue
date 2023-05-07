<template>
    <ContentField>
        <table class="table table-striped table-hover" style="text-align: left;">
            <thead>
                <tr>
                    <th>
                        <canvas id="1" width="30" height="30"></canvas>
                    </th>
                    <th>
                        <canvas id="2" width="30" height="30"></canvas>
                    </th>
                    <th>结果</th>
                    <th>对战时间</th>
                    <th>操作</th>
                </tr>
            </thead>
            <tbody>
                <tr v-for="record in records" :key="record.record.id">
                    <td>
                        <img :src="record.a_photo" alt="" class="record-user-photo">
                        &nbsp;
                        <span class="record-user-username">{{ record.a_username }}</span>
                    </td>
                    <td>
                        <img :src="record.b_photo" alt="" class="record-user-photo">
                        &nbsp;
                        <span class="record-user-username">{{ record.b_username }}</span>    
                    </td>
                    <td class="align-middle">{{ record.result }}</td>
                    <td class="align-middle">
                        {{ record.record.createtime }}
                    </td>
                    <td class="align-middle">
                        <button @click="open_record_content(record.record.id)" type="button" class="btn btn-warning">查看录像</button>
                    </td>
                </tr>
            </tbody>
        </table>
        <nav aria-label="...">
            <ul class="pagination" style="float: right;">
                <li class="page-item">
                    <a class="page-link" href="#" @click="click_page(1)">
                        <span aria-hidden="true">&laquo;</span>
                    </a>
                </li>
                <li class="page-item" @click="click_page(-2)">
                    <a class="page-link" href="#">前一页</a>
                </li>
                <li :class="'page-item ' + page.is_active" v-for="page in pages" :key="page.number" @click="click_page(page.number)">
                    <a class="page-link" href="#">{{ page.number }}</a>
                </li>
                <li class="page-item" @click="click_page(-1)">
                    <a class="page-link" href="#">后一页</a>
                </li>
                <li class="page-item" @click="click_page(0)">
                    <a class="page-link" href="#">
                        <span aria-hidden="true">&raquo;</span>
                    </a>
                </li>
            </ul>
        </nav>
    </ContentField>
</template>

<script>
import ContentField from '../../components/ContentField.vue'
import { useStore } from 'vuex'
import { ref, onMounted } from 'vue'
import $ from 'jquery'
import router from '../../router/index'


export default {
    components: {
        ContentField
    },
    setup() {
        const store = useStore();
        let records = ref([]);
        let current_page = 1;
        let total_records = 0;
        let pages = ref([]);

        const click_page = page => {
            let max_pages = parseInt(Math.ceil(total_records / 10));
            if (page === -2) page = current_page - 1;
            else if (page === -1) page = current_page + 1;
            else if (page === 0) page = max_pages;

            if(page >= 1 && page <= max_pages) {
                pull_page(page);
            }
        }

        const update_pages = () => {
            let max_pages = parseInt(Math.ceil(total_records / 10));
            let new_pages = [];
            for(let i = current_page - 2; i <= current_page + 2; i++) {
                if(i >= 1 && i <= max_pages) {
                    new_pages.push({
                        number: i,
                        is_active: i === current_page ? "active" : "",
                    });
                }
            }
            pages.value = new_pages;
        }

        const pull_page = page => {//查询某一个页面的内容
            current_page = page;
            $.ajax({
                url: "https://app4904.acapp.acwing.com.cn/api/record/getlist/",
                data: {
                    page,
                },
                type: "get",
                headers: {
                    Authorization: "Bearer " + store.state.user.token,
                },
                success(resp) {
                    records.value = resp.records;
                    total_records = resp.records_count;
                    update_pages();
                },
                error(resp) {
                    console.log(resp);
                }
            })
        }

        pull_page(current_page);

        const stringTo2D = map => {
            let g = [];
            for(let i = 0, k = 0; i < 13; i++) {
                let line = [];
                for(let j = 0; j < 14; j++, k++) {
                    if(map[k] === '0') line.push(0);
                    else line.push(1);
                }
                g.push(line);
            }
            return g;
        }

        const open_record_content = recordId => {
            for(const record of records.value) {
                if(record.record.id === recordId) {
                    store.commit("updateIsRecord", true);
                    console.log(record);
                    store.commit("updateGamemap", {
                        map: stringTo2D(record.record.map),
                        a_username: record.a_username,
                        a_id: record.record.aid,
                        a_sx: record.record.asx,
                        a_sy: record.record.asy,
                        b_username: record.b_username,
                        b_id: record.record.bid,
                        b_sx: record.record.bsx,
                        b_sy: record.record.bsy,
                    });
                    store.commit("updateRecordId", recordId);
                    // 先画出地图，再更新steps
                    router.push({
                        name: "record_content",
                        params: {
                            recordId
                        }
                    })
                    store.commit("updateSteps", {
                        a_steps: record.record.asteps,
                        b_steps: record.record.bsteps,
                    });
                    store.commit("updateRecordLoser", record.record.loser);
                    break;
                }
            }
        }

        onMounted( () => {
            var canvas = document.getElementById("1");
            var ctx1 = canvas.getContext("2d");
            ctx1.beginPath();
            ctx1.arc(15, 15, 13, 0, 2 * Math.PI);
            ctx1.fillStyle = "#28983D";
            ctx1.fill();
            ctx1.beginPath();
            ctx1.arc(9, 15, 2, 0, 2 * Math.PI);
            ctx1.fillStyle = "black";
            ctx1.fill();
            ctx1.beginPath();
            ctx1.arc(21, 15, 2, 0, 2 * Math.PI);
            ctx1.fillStyle = "black";
            ctx1.fill();
            canvas = document.getElementById("2");
            var ctx2 = canvas.getContext("2d");
            ctx2.beginPath();
            ctx2.arc(15, 15, 13, 0, 2 * Math.PI);
            ctx2.fillStyle = "#BD0303";
            ctx2.fill();
            ctx2.beginPath();
            ctx2.arc(9, 15, 2, 0, 2 * Math.PI);
            ctx2.fillStyle = "black";
            ctx2.fill();
            ctx2.beginPath();
            ctx2.arc(21, 15, 2, 0, 2 * Math.PI);
            ctx2.fillStyle = "black";
            ctx2.fill();
        });

        return {
            records,
            open_record_content,
            pages,
            click_page,
        }
    }
}
</script>

<style scoped>
img.record-user-photo {
    width: 10vmin;
    border-radius: 50%;
}
</style>