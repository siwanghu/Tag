<template>
  <div class="layout">
    <Layout>
      <Header>
        <Menu mode="horizontal" theme="dark" active-name="1">
          <MenuItem name="3" @click.native="loginShow">
            <Icon type="md-chatbubbles" />
            登陆
          </MenuItem>
          <MenuItem name="4" @click.native="downloadSpit">
            <a href="/static/app-release.apk" download="ws-record">
            <Icon type="ios-cloud-download-outline" />
            下载
            </a>
          </MenuItem>
          <div class="layout-nav">
            <DatePicker type="date" placeholder="请选择时间" style="width: 200px" @on-change="changeDate" ></DatePicker>
            <MenuItem name="1" @click.native="upload">
              <Icon type="ios-analytics" ></Icon>
              切分
            </MenuItem>
            <MenuItem name="2" @click.native="loadData">
              <Icon type="ios-paper" ></Icon>
              刷新
            </MenuItem>
          </div>
        </Menu>
      </Header>
      <Content :style="{padding: '0 50px'}">
        <Card>
          <Collapse>
            <Panel name="1">
              操作手册
              <p slot="content">
                &nbsp;&nbsp;&nbsp;&nbsp;1.&nbsp;<span style="font-weight:bold">check</span>&nbsp;&nbsp;代表符合录音规范的数据<br>
                &nbsp;&nbsp;&nbsp;&nbsp;2.&nbsp;<span style="font-weight:bold">uncheck</span>&nbsp;&nbsp;代表不符合录音规范的数据<br>
                &nbsp;&nbsp;&nbsp;&nbsp;3.&nbsp;先试听录音，如果录音符合规范，点击播放界面上的<span style="font-weight:bold">check</span>&nbsp;&nbsp;；如果录音不符合规范，点击播放界面上的<span style="font-weight:bold">uncheck</span>&nbsp;&nbsp;<br>
                &nbsp;&nbsp;&nbsp;&nbsp;4.&nbsp;如果您不确定录音是否符合规范，点击播放界面上的<span style="font-weight:bold">退出</span>按钮，并麻烦您自己记录录音ID，联系管理员咨询<br>
                &nbsp;&nbsp;&nbsp;&nbsp;5.&nbsp;我们要求规范录音为包含“小微小微”4个完整汉字
              </p>
            </Panel>
            <Panel name="2">
              不规范录音说明
              <p slot="content">
                &nbsp;&nbsp;&nbsp;&nbsp;1.&nbsp;录音中不含有任何汉字<br>
                &nbsp;&nbsp;&nbsp;&nbsp;2.&nbsp;录音中空白或仅含噪音<br>
                &nbsp;&nbsp;&nbsp;&nbsp;3.&nbsp;录音中不足一个完整的“小微小微”或含有多个“小微小微”<br>
              </p>
            </Panel>
          </Collapse>
        </Card>
        <Card>
          <div style="min-height: 820px;">
            <Table border :columns="VoiceColumns" :data="files"></Table>
            <Page :total="total" :page-size ="size" :current="page+1" show-elevator @on-change="handlePage"/>
          </div>
        </Card>
      </Content>
      <Footer class="layout-footer-center">版权所有&copy; 苏州蛙声科技</Footer>
    </Layout>
    <Drawer title="登陆界面" width="500" :closable="false" :mask-closable="false" v-model="loginflag">
      <Row>
      <Icon type="ios-person" size="20"/>账号:
      <Input v-model="username" placeholder="请输入用户名" style="width: 300px" />
      </Row>
      <br><br>
      <Row>
      <Icon type="ios-key-outline" size="20"/>密码:
      <Input v-model="password" type="password" placeholder="请出入密码" style="width: 300px" />
      </Row>
      <br><br>
      <Row style="text-align:right;">
      <Button style="margin-right: 20px" size="large"  @click="loginflag=false">取消</Button>
      <Button style="margin-right: 120px" type="primary" size="large"  @click="loginServer">登陆</Button>
      </Row>
    </Drawer>
    <Modal
      v-model="modal_download"
      title="请选择阿里云上OSS数据目录(以切分录音时间命名)"
      :mask-closable="false"
      :closable="false"
      @on-ok="ok_split"
      @on-cancel="cancel_split">
      <Row>
        <Scroll >
          <Card dis-hover v-for="(item, index) in splitlist" :key="index" style="margin: 32px 0">
            <Icon type="ios-albums-outline" />&nbsp;&nbsp;&nbsp;&nbsp;
            <Button @click="splitclick(item)">&nbsp;&nbsp;&nbsp; {{ item }}</Button>
          </Card>
        </Scroll>
      </Row>
    </Modal>
    <Modal
      v-model="modal_split"
      title="录音文件"
      :mask-closable="false"
      :closable="false">
      <Row>
        <Scroll >
          <Card dis-hover v-for="(item, index) in splitvoicelist" :key="index" style="margin: 32px 0">
            <Icon type="ios-barcode-outline" /><br>
            文件ID:&nbsp;&nbsp; {{ item.id }}<br>
            文件名:&nbsp;&nbsp; {{ item.name }}<br>
            文件大小:&nbsp;&nbsp; {{ item.size }}<br>
            文件状态:&nbsp;&nbsp; {{ item.commit }}<br>
            文件标签:&nbsp;&nbsp; {{ item.label }}
          </Card>
        </Scroll>
      </Row>
      <br>
      <Row>
        <Button shape="circle" @click="splitdown">下载</Button>
      </Row>
      <div slot="footer">
        <Button  type="primary" @click="ok_splitvoicelist" >退出</Button>
      </div>
    </Modal>
    <Modal
      v-model="modal_upload"
      title="请选择阿里云上OSS数据目录(以采集录音时间命名)"
      :mask-closable="false"
      :closable="false"
      @on-ok="ok_upload"
      @on-cancel="cancel_upload">
      <Row>
        <Scroll >
          <Card dis-hover v-for="(item, index) in datelist" :key="index" style="margin: 32px 0">
            <Icon type="ios-albums-outline" />&nbsp;&nbsp;&nbsp;&nbsp;
            <Button @click="dirclick(item)">&nbsp;&nbsp;&nbsp; {{ item }}</Button>
          </Card>
        </Scroll>
      </Row>
      <Row>
        <Button shape="circle" @click="Analysis">数据分析</Button>
      </Row>
    </Modal>
    <Modal
      v-model="modal_checkbox"
      title="录音目录"
      :mask-closable="false"
      :closable="false">
      <Scroll >
      <CheckboxGroup v-model="analysis_list" v-for="(item, index) in datelist">
        <Checkbox v-bind:label="item" size="large">
          <span>{{item}}</span>
        </Checkbox>
      </CheckboxGroup>
      </Scroll>
      <div slot="footer">
        <Button shape="circle" @click="ok_modal_checkbox">分析</Button>
        <Button  type="primary" @click="cancel_modal_checkbox" >退出</Button>
      </div>
    </Modal>
    <Modal
      v-model="modal_voice"
      title="目录"
      :mask-closable="false"
      :closable="false">
      {{this.dir}}<br>
      <Row>
        <Scroll>
          <Card dis-hover v-for="(item, index) in voicelist" :key="index" style="margin: 32px 0">
            <Icon type="ios-barcode-outline" />&nbsp;&nbsp;&nbsp;{{ item }}
          </Card>
        </Scroll>
      </Row>
      <br>
      <Row>
        <Button shape="circle" @click="split">切分</Button>
      </Row>
      <div slot="footer">
        <Button  type="primary" @click="ok_voicelist" >退出</Button>
      </div>
    </Modal>
    <Modal
      v-model="modal_analysis"
      title="数据分析"
      :mask-closable="false"
      :closable="false">
      <Row>
        {{patterns[0]}}:&nbsp;&nbsp;
        <Dropdown trigger="click" style="margin-left: 20px">
          <a href="javascript:void(0)">
            0.2m
            <Icon type="ios-arrow-down"></Icon>
          </a>
          <DropdownMenu slot="list"  dis-hover v-for="(item, index) in people_0_2_0" :key="index">
            <DropdownItem>{{item}}</DropdownItem>
          </DropdownMenu>
        </Dropdown>
        <Dropdown trigger="click" style="margin-left: 20px">
          <a href="javascript:void(0)">
            0.8m
            <Icon type="ios-arrow-down"></Icon>
          </a>
          <DropdownMenu slot="list"  dis-hover v-for="(item, index) in people_0_8_0" :key="index">
            <DropdownItem>{{item}}</DropdownItem>
          </DropdownMenu>
        </Dropdown>
        <Dropdown trigger="click" style="margin-left: 20px">
          <a href="javascript:void(0)">
            1.5m
            <Icon type="ios-arrow-down"></Icon>
          </a>
          <DropdownMenu slot="list"  dis-hover v-for="(item, index) in people_1_5_0" :key="index">
            <DropdownItem>{{item}}</DropdownItem>
          </DropdownMenu>
        </Dropdown>
        <Dropdown trigger="click" style="margin-left: 20px">
          <a href="javascript:void(0)">
            2.0m
            <Icon type="ios-arrow-down"></Icon>
          </a>
          <DropdownMenu slot="list"  dis-hover v-for="(item, index) in people_2_0_0" :key="index">
            <DropdownItem>{{item}}</DropdownItem>
          </DropdownMenu>
        </Dropdown>
        <Dropdown trigger="click" style="margin-left: 20px">
          <a href="javascript:void(0)">
            4.0m
            <Icon type="ios-arrow-down"></Icon>
          </a>
          <DropdownMenu slot="list"  dis-hover v-for="(item, index) in people_4_0_0" :key="index">
            <DropdownItem>{{item}}</DropdownItem>
          </DropdownMenu>
        </Dropdown>
      </Row>
      <Row>
        {{patterns[1]}}:&nbsp;&nbsp;
        <Dropdown trigger="click" style="margin-left: 20px">
          <a href="javascript:void(0)">
            0.2m
            <Icon type="ios-arrow-down"></Icon>
          </a>
          <DropdownMenu slot="list"  dis-hover v-for="(item, index) in people_0_2_1" :key="index">
            <DropdownItem>{{item}}</DropdownItem>
          </DropdownMenu>
        </Dropdown>
        <Dropdown trigger="click" style="margin-left: 20px">
          <a href="javascript:void(0)">
            0.8m
            <Icon type="ios-arrow-down"></Icon>
          </a>
          <DropdownMenu slot="list"  dis-hover v-for="(item, index) in people_0_8_1" :key="index">
            <DropdownItem>{{item}}</DropdownItem>
          </DropdownMenu>
        </Dropdown>
        <Dropdown trigger="click" style="margin-left: 20px">
          <a href="javascript:void(0)">
            1.5m
            <Icon type="ios-arrow-down"></Icon>
          </a>
          <DropdownMenu slot="list"  dis-hover v-for="(item, index) in people_1_5_1" :key="index">
            <DropdownItem>{{item}}</DropdownItem>
          </DropdownMenu>
        </Dropdown>
        <Dropdown trigger="click" style="margin-left: 20px">
          <a href="javascript:void(0)">
            2.0m
            <Icon type="ios-arrow-down"></Icon>
          </a>
          <DropdownMenu slot="list"  dis-hover v-for="(item, index) in people_2_0_1" :key="index">
            <DropdownItem>{{item}}</DropdownItem>
          </DropdownMenu>
        </Dropdown>
        <Dropdown trigger="click" style="margin-left: 20px">
          <a href="javascript:void(0)">
            4.0m
            <Icon type="ios-arrow-down"></Icon>
          </a>
          <DropdownMenu slot="list"  dis-hover v-for="(item, index) in people_4_0_1" :key="index">
            <DropdownItem>{{item}}</DropdownItem>
          </DropdownMenu>
        </Dropdown>
      </Row>
      <Row>
        {{patterns[2]}}:&nbsp;&nbsp;
        <Dropdown trigger="click" style="margin-left: 20px">
          <a href="javascript:void(0)">
            0.2m
            <Icon type="ios-arrow-down"></Icon>
          </a>
          <DropdownMenu slot="list"  dis-hover v-for="(item, index) in people_0_2_2" :key="index">
            <DropdownItem>{{item}}</DropdownItem>
          </DropdownMenu>
        </Dropdown>
        <Dropdown trigger="click" style="margin-left: 20px">
          <a href="javascript:void(0)">
            0.8m
            <Icon type="ios-arrow-down"></Icon>
          </a>
          <DropdownMenu slot="list"  dis-hover v-for="(item, index) in people_0_8_2" :key="index">
            <DropdownItem>{{item}}</DropdownItem>
          </DropdownMenu>
        </Dropdown>
        <Dropdown trigger="click" style="margin-left: 20px">
          <a href="javascript:void(0)">
            1.5m
            <Icon type="ios-arrow-down"></Icon>
          </a>
          <DropdownMenu slot="list"  dis-hover v-for="(item, index) in people_1_5_2" :key="index">
            <DropdownItem>{{item}}</DropdownItem>
          </DropdownMenu>
        </Dropdown>
        <Dropdown trigger="click" style="margin-left: 20px">
          <a href="javascript:void(0)">
            2.0m
            <Icon type="ios-arrow-down"></Icon>
          </a>
          <DropdownMenu slot="list"  dis-hover v-for="(item, index) in people_2_0_2" :key="index">
            <DropdownItem>{{item}}</DropdownItem>
          </DropdownMenu>
        </Dropdown>
        <Dropdown trigger="click" style="margin-left: 20px">
          <a href="javascript:void(0)">
            4.0m
            <Icon type="ios-arrow-down"></Icon>
          </a>
          <DropdownMenu slot="list"  dis-hover v-for="(item, index) in people_4_0_2" :key="index">
            <DropdownItem>{{item}}</DropdownItem>
          </DropdownMenu>
        </Dropdown>
      </Row>
      <Row>
        <Icon type="ios-people" size="20"/>&nbsp;人员数目:&nbsp;&nbsp;{{people.length}}
        &nbsp;&nbsp; &nbsp;&nbsp;
        <Icon type="ios-people" size="20"/>&nbsp;录音条数:&nbsp;&nbsp;{{analysis_voicelist.length}}
        <Dropdown trigger="click" style="margin-left: 20px">
          <a href="javascript:void(0)">
            人员名单
            <Icon type="ios-arrow-down"></Icon>
          </a>
          <DropdownMenu slot="list"  dis-hover v-for="(item, index) in people" :key="index">
            <DropdownItem>{{item}}</DropdownItem>
          </DropdownMenu>
        </Dropdown>
      </Row>
      <br>
      <Row>
        {{patterns[0]}}-0.2m&nbsp;&nbsp;{{diff_0_2_0}}&nbsp;&nbsp;没有采集到
      </Row>
      <br>
      <Row>
        {{patterns[0]}}-0.8m&nbsp;&nbsp;{{diff_0_8_0}}&nbsp;&nbsp;没有采集到
      </Row>
      <br>
      <Row>
        {{patterns[0]}}-1.5m&nbsp;&nbsp;{{diff_1_5_0}}&nbsp;&nbsp;没有采集到
      </Row>
      <br>
      <Row>
        {{patterns[0]}}-2.0m&nbsp;&nbsp;{{diff_2_0_0}}&nbsp;&nbsp;没有采集到
      </Row>
      <br>
      <Row>
        {{patterns[0]}}-4.0m&nbsp;&nbsp;{{diff_4_0_0}}&nbsp;&nbsp;没有采集到
      </Row>
      <br>
      <Row>
        {{patterns[1]}}-0.2m&nbsp;&nbsp;{{diff_0_2_1}}&nbsp;&nbsp;没有采集到
      </Row>
      <br>
      <Row>
        {{patterns[1]}}-0.8m&nbsp;&nbsp;{{diff_0_8_1}}&nbsp;&nbsp;没有采集到
      </Row>
      <br>
      <Row>
        {{patterns[1]}}-1.5m&nbsp;&nbsp;{{diff_1_5_1}}&nbsp;&nbsp;没有采集到
      </Row>
      <br>
      <Row>
        {{patterns[1]}}-2.0m&nbsp;&nbsp;{{diff_2_0_1}}&nbsp;&nbsp;没有采集到
      </Row>
      <br>
      <Row>
        {{patterns[1]}}-4.0m&nbsp;&nbsp;{{diff_4_0_1}}&nbsp;&nbsp;没有采集到
      </Row>
      <br>
      <Row>
        {{patterns[2]}}-0.2m&nbsp;&nbsp;{{diff_0_2_2}}&nbsp;&nbsp;没有采集到
      </Row>
      <br>
      <Row>
        {{patterns[2]}}-0.8m&nbsp;&nbsp;{{diff_0_8_2}}&nbsp;&nbsp;没有采集到
      </Row>
      <br>
      <Row>
        {{patterns[2]}}-1.5m&nbsp;&nbsp;{{diff_1_5_2}}&nbsp;&nbsp;没有采集到
      </Row>
      <br>
      <Row>
        {{patterns[2]}}-2.0m&nbsp;&nbsp;{{diff_2_0_2}}&nbsp;&nbsp;没有采集到
      </Row>
      <br>
      <Row>
        {{patterns[2]}}-4.0m&nbsp;&nbsp;{{diff_4_0_2}}&nbsp;&nbsp;没有采集到
      </Row>
      <div slot="footer">
        <Button  type="primary" @click="cancel_analysis_voicelist" >退出</Button>
      </div>
    </Modal>
    <Modal v-model="modal_step" width="1000" :mask-closable="false"
           :closable="false">
      <p slot="header">
        <Icon type="ios-bicycle" />
        <span>任务进度条</span>
      </p>
      <div style="text-align:center">
        <br>
        <Row>
          <Spin fix>
            <Icon type="ios-loading" size=30 class="demo-spin-icon-load"></Icon>
            <div>请耐心等待，任务执行中.....</div>
          </Spin>
        </Row>
        <br>
        <br>
        <Row>
        <Steps :current="step">
          <Step title="任务提交" icon="logo-reddit" content="创建一个录音切分任务"></Step>
          <Step title="录音下载" icon="ios-subway-outline" content="从阿里云下载指定录音文件"></Step>
          <Step title="录音切分" icon="logo-tux" content="对录音文件进行切分"></Step>
          <Step title="录音存储" icon="ios-albums-outline" content="切分好的录音存储到阿里云"></Step>
          <Step title="任务结束" icon="md-bulb" content="成功执行录音切分任务"></Step>
        </Steps>
        </Row>
        <br>
        <br>
      </div>
      <div slot="footer">
      </div>
    </Modal>
    <Modal
      v-model="modal_remove"
      title="删除框"
      :mask-closable="false"
      :closable="false"
      @on-ok="ok_remove"
      @on-cancel="cancel_remove">
      <p>真的要删除吗，不在考虑一下？</p>
    </Modal>
    <Modal
      v-model="modal_update"
      title="修改框"
      :mask-closable="false"
      :closable="false"
      @on-ok="ok_update"
      @on-cancel="cancel_update">
      <Row>
        <Icon type="ios-browsers-outline" size="20"/>ID：{{fileId}}
      </Row>
      <br>
      <Row>
        <Icon type="ios-folder-outline" size="20" />文件名：
        <Input v-model="filename" disabled placeholder="Enter something..." style="width: 300px" />
      </Row>
      <br>
      <Row>
        <Icon type="ios-hand-outline" size="20"/>标签：
        <Input v-model="filelabel" placeholder="Enter something..." style="width: 300px" />
      </Row>
    </Modal>
    <Modal
      v-model="modal_play"
      title="播放框"
      :mask-closable="false"
      :closable="false">
      <div class="demo-spin-article">
        <Row>
          <Icon type="ios-browsers-outline" size="20"/>ID：{{fileId}}
        </Row>
        <br>
        <Row>
          <Icon type="ios-folder-outline" size="20" />文件名：{{filename}}
        </Row>
        <br>
        <Row>
           <Icon type="ios-hand-outline" size="20"/>标签：{{filelabel}}
        </Row>
        <br>
        <Row>
          <audio v-bind:src="fileurl" id="audio"></audio>
        </Row>
        <br>
        <br>
        <Row>
          <Button shape="circle" @click="play">播放</Button>
        </Row>
      </div>
      <div slot="footer">
        <Button  type="text" @click="ok_play">check</Button>
        <Button  type="primary" @click="uncheck_play">uncheck</Button>
        <Button  type="primary" @click="cancel_play">退出</Button>
      </div>
    </Modal>
  </div>
</template>
<script>
  import axios from 'axios'
  import JSZip from 'jszip'
  import FileSaver from 'file-saver'

  function addZero(v) {
    return v < 10 ? `0${v}` : v;
  }
  const getFile = url => {
    return new Promise((resolve, reject) => {
      axios({
        method:'get',
        url,
        responseType: 'arraybuffer',
        dataType: 'jsonp',
        crossDomain: true,
      }).then(data => {
        resolve(data.data)
      }).catch(error => {
        reject(error.toString())
      })
    })
  }
  export default {
    props: {
      Audio: {
        type: Object,
      },
    },
    data () {
      return {
        datetime:"2000-12-01",
        fileId:0,
        filename:"",
        filelabel:"",
        filecheck:false,
        fileurl:"",
        total:100,
        size:10,
        page:0,
        playflag:false,
        loginflag:false,
        islisten:false,
        files: [],
        datelist: [],
        splitlist: [],
        voicelist:[],
        splitvoicelist:[],
        analysis_list:[],
        analysis_voicelist:[],
        downloadurl:[],
        dir:"",
        patterns:[],
        people:[],
        people_0_2_0:[],
        people_0_8_0:[],
        people_1_5_0:[],
        people_2_0_0:[],
        people_4_0_0:[],
        people_0_2_1:[],
        people_0_8_1:[],
        people_1_5_1:[],
        people_2_0_1:[],
        people_4_0_1:[],
        people_0_2_2:[],
        people_0_8_2:[],
        people_1_5_2:[],
        people_2_0_2:[],
        people_4_0_2:[],
        diff_0_2_0:[],
        diff_0_8_0:[],
        diff_1_5_0:[],
        diff_2_0_0:[],
        diff_4_0_0:[],
        diff_0_2_1:[],
        diff_0_8_1:[],
        diff_1_5_1:[],
        diff_2_0_1:[],
        diff_4_0_1:[],
        diff_0_2_2:[],
        diff_0_8_2:[],
        diff_1_5_2:[],
        diff_2_0_2:[],
        diff_4_0_2:[],
        splitdir:"",
        urls:"/assets/app-debug.apk",
        step:0,
        username:"",
        password:"",
        modal_remove:false,
        modal_update:false,
        modal_play:false,
        modal_upload:false,
        modal_download:false,
        modal_voice:false,
        modal_split:false,
        modal_step:false,
        modal_analysis:false,
        modal_checkbox:false,
        value: 0,
        auObj: null,
        logintime:0,
        currentminutetime: 0,
        currentsecondtime: '00',
        allminutetime: 0,
        allsecondtime: '00',
        VoiceColumns: [
          {
            title: 'ID',
            key: 'id',
            render: (h, params) => {
              return h('div', [
                h('Icon', {
                  props: {
                    type: 'person'
                  }
                }),
                h('strong', params.row.id)
              ]);
            }
          },
          {
            title: '文件名',
            key: 'name'
          },
          {
            title: '大小',
            key: 'size'
          },
          {
            title: '标签',
            key: 'label'
          },
          {
            title: '检查',
            key: 'commit'
          },
          {
            title: '操作',
            key: 'action',
            width: 200,
            align: 'center',
            render: (h, params) => {
              return h('div', [
                h('Button', {
                  props: {
                    type: 'primary',
                    size: 'small'
                  },
                  style: {
                    marginRight: '10px'
                  },
                  on: {
                    click: () => {
                      this.show(params.row)
                    }
                  }
                }, '试听'),
                h('Button', {
                  props: {
                    type: 'primary',
                    size: 'small'
                  },
                  style: {
                    marginRight: '10px'
                  },
                  on: {
                    click: () => {
                      this.update(params.row)
                    }
                  }
                }, '修改'),
                h('Button', {
                  props: {
                    type: 'error',
                    size: 'small'
                  },
                  on: {
                    click: () => {
                      this.remove(params.row)
                    }
                  }
                }, '删除')
              ]);
            }
          }
        ],
      }
    },
    created(){
      this.loadpage();
    },
    mounted() {
      this.auObj = document.getElementById('audio');
      this.auObj.ontimeupdate = () => {
        this.value = (this.auObj.currentTime / this.auObj.duration) * 100;
        this.currentminutetime = (Math.round(this.auObj.currentTime) - (Math.round(this.auObj.currentTime) % 60)) / 60;
        this.currentsecondtime = addZero(Math.round(this.auObj.currentTime) % 60);
        this.allminutetime = (this.auObj.duration - (this.auObj.duration % 60)) / 60;
        this.allsecondtime = addZero(Math.round(this.auObj.duration) % 60);
      };
    },
    methods: {
      login(){
        let _self = this;
        this.$axios.get("/voice/login", {
          params: {
            username:_self.username,
            password:_self.password,
          }
        }).then(result => {
          console.log(result);
          if(result.data==true){
            this.$Message.success("登陆成功!");
            this.logintime=1;
            this.loginflag=false;
          }else{
            this.$Message.error("用户名或密码错误!");
            this.logintime=0;
            this.loginflag=true;
          }
        })
          .catch(err => {});
      },
      downloadSpit(){
        // if(this.logintime==1){
        //   this.loadsplitdir();
        //   this.modal_download=true;
        // }else{
        //   this.$Message.warning("请先登陆!");
        // }
        //window.location.href = this.urls;
      },
      loadpage(){
        let _self = this;
        this.$axios.get("/voice/page", {
          params: {
            datetime:_self.datetime,
            page:_self.page,
            size:10,
          }
        }).then(result => {
          console.log(result);
          _self.files=result.data.content;
          console.log(_self.files);
          _self.total=result.data.totalElements;
        })
          .catch(err => {});
      },
      updates(){
        let _self = this;
        this.$axios.get("/voice/update", {
          params: {
            id:_self.fileId,
            name:_self.filename,
            label:_self.filelabel,
          }
        }).then(result => {
          console.log(result);
          _self.loadpage();
          //this.$Message.success('修改成功！');
        })
          .catch(err => {});
      },
      delete(){
        let _self = this;
        this.$axios.get("/voice/delete", {
          params: {
            id:_self.fileId,
          }
        }).then(result => {
          console.log(result);
          if(result.data==true){
            _self.loadpage();
            this.$Message.success('删除成功！');
          }else{
            this.$Message.warning("服务器正忙,删除失败!")
          }
        })
          .catch(err => {});
      },
      uncheck(){
        let _self = this;
        this.$axios.get("/voice/check", {
          params: {
            id:_self.fileId,
            commit:"uncheck",
          }
        }).then(result => {
          console.log(result);
          if(result.data==true){
            this.filelabel="已处理";
            this.updates();
            _self.loadpage();
            this.$Message.success('uncheck成功！');
            this.modal_play=false;
            this.islisten=false;
          }else{
            this.$Message.warning("服务器正忙,确认失败!")
          }
        })
          .catch(err => {});
      },
      check(){
        let _self = this;
        this.$axios.get("/voice/check", {
          params: {
            id:_self.fileId,
            commit:"check",
          }
        }).then(result => {
          console.log(result);
          if(result.data==true){
            this.filelabel="已处理";
            this.updates();
            _self.loadpage();
            this.$Message.success('check成功！');
            this.modal_play=false;
            this.islisten=false;
          }else{
            this.$Message.warning("服务器正忙,确认失败!")
          }
        })
          .catch(err => {});
      },
      loaddir(){
        let _self = this;
        this.$axios.get("/voice/dir", {
          params: {
          }
        }).then(result => {
          console.log(result);
          this.datelist=result.data;
        })
          .catch(err => {});
      },
      loadsplitdir(){
        let _self = this;
        this.$axios.get("/voice/splitdir", {
          params: {
          }
        }).then(result => {
          console.log(result);
          this.splitlist=result.data;
        })
          .catch(err => {});
      },
      loadVoice(){
        let _self = this;
        this.$axios.get("/voice/file", {
          params: {
            dir:_self.dir
          }
        }).then(result => {
          console.log(result);
          this.voicelist=result.data;
        })
          .catch(err => {});
      },
      loadVoices(lists){
        console.log("选中的目录："+lists);
        let _self = this;
        this.$axios.get("/voice/file", {
          params: {
            dir:lists[0]
          }
        }).then(result => {
          console.log("删除前:"+lists);
          console.log(result);
          lists.splice(0,1);
          console.log("删除后:"+lists);
          this.voicelist=result.data;
          for(var i=0;i<this.voicelist.length;i++){
            this.analysis_voicelist.push(this.voicelist[i]);
          }
          console.log("数据长度："+this.analysis_voicelist.length);
          if(lists.length>0){
            this.loadVoices(lists);
          }else{
            for(var i=0;i<this.analysis_voicelist.length;i++){
              var array=this.analysis_voicelist[i].split("-");
              var disname=array[2]+"-"+array[4]+"-"+array[5];
              var name=array[5];
              var pat=array[4];
              if(this.patterns.indexOf(pat)<0){
                this.patterns.push(pat);
              }
              if(this.people.indexOf(name)<0) {
                this.people.push(name);
              }
              if(disname.indexOf("0.2m")>=0&&disname.indexOf(this.patterns[0])>=0){
                this.people_0_2_0.push(disname.split("-")[2])
              }
              else if(disname.indexOf("0.8m")>=0&&disname.indexOf(this.patterns[0])>=0){
                this.people_0_8_0.push(disname.split("-")[2])
              }
              else if(disname.indexOf("1.5m")>=0&&disname.indexOf(this.patterns[0])>=0){
                this.people_1_5_0.push(disname.split("-")[2])
              }
              else if(disname.indexOf("2.0m")>=0&&disname.indexOf(this.patterns[0])>=0){
                this.people_2_0_0.push(disname.split("-")[2])
              }
              else if(disname.indexOf("4.0m")>=0&&disname.indexOf(this.patterns[0])>=0){
                this.people_4_0_0.push(disname.split("-")[2])
              }
              else if(disname.indexOf("0.2m")>=0&&disname.indexOf(this.patterns[1])>=0){
                this.people_0_2_1.push(disname.split("-")[2])
              }
              else if(disname.indexOf("0.8m")>=0&&disname.indexOf(this.patterns[1])>=0){
                this.people_0_8_1.push(disname.split("-")[2])
              }
              else if(disname.indexOf("1.5m")>=0&&disname.indexOf(this.patterns[1])>=0){
                this.people_1_5_1.push(disname.split("-")[2])
              }
              else if(disname.indexOf("2.0m")>=0&&disname.indexOf(this.patterns[1])>=0){
                this.people_2_0_1.push(disname.split("-")[2])
              }
              else if(disname.indexOf("4.0m")>=0&&disname.indexOf(this.patterns[1])>=0){
                this.people_4_0_1.push(disname.split("-")[2])
              }
              else if(disname.indexOf("0.2m")>=0&&disname.indexOf(this.patterns[2])>=0){
                this.people_0_2_2.push(disname.split("-")[2])
              }
              else if(disname.indexOf("0.8m")>=0&&disname.indexOf(this.patterns[2])>=0){
                this.people_0_8_2.push(disname.split("-")[2])
              }
              else if(disname.indexOf("1.5m")>=0&&disname.indexOf(this.patterns[2])>=0){
                this.people_1_5_2.push(disname.split("-")[2])
              }
              else if(disname.indexOf("2.0m")>=0&&disname.indexOf(this.patterns[2])>=0){
                this.people_2_0_2.push(disname.split("-")[2])
              }
              else if(disname.indexOf("4.0m")>=0&&disname.indexOf(this.patterns[2])>=0){
                this.people_4_0_2.push(disname.split("-")[2])
              }
            }
            this.diff_0_2_0=this.array_difference(this.people,this.people_0_2_0);
            this.diff_0_8_0=this.array_difference(this.people,this.people_0_8_0);
            this.diff_1_5_0=this.array_difference(this.people,this.people_1_5_0);
            this.diff_2_0_0=this.array_difference(this.people,this.people_2_0_0);
            this.diff_4_0_0=this.array_difference(this.people,this.people_4_0_0);

            this.diff_0_2_1=this.array_difference(this.people,this.people_0_2_1);
            this.diff_0_8_1=this.array_difference(this.people,this.people_0_8_1);
            this.diff_1_5_1=this.array_difference(this.people,this.people_1_5_1);
            this.diff_2_0_1=this.array_difference(this.people,this.people_2_0_1);
            this.diff_4_0_1=this.array_difference(this.people,this.people_4_0_1);

            this.diff_0_2_2=this.array_difference(this.people,this.people_0_2_2);
            this.diff_0_8_2=this.array_difference(this.people,this.people_0_8_2);
            this.diff_1_5_2=this.array_difference(this.people,this.people_1_5_2);
            this.diff_2_0_2=this.array_difference(this.people,this.people_2_0_2);
            this.diff_4_0_2=this.array_difference(this.people,this.people_4_0_2);
            console.log(this.people);
            console.log(this.patterns);
          }
        })
          .catch(err => {});
      },
      loadSplitVoice(){
        let _self = this;
        this.$axios.get("/voice/splitfile", {
          params: {
            splitdir:_self.splitdir
          }
        }).then(result => {
          console.log(result);
          console.log("长度"+result.data.length);
          if(result.data.length==0){
            this.splitvoicelist=["当前时间点目录下没有check的录音数据，请先check之后在下载"];
          }else{
            this.splitvoicelist=result.data;
          }
        })
          .catch(err => {});
      },
      clearVoice(){
        let _self = this;
        this.$axios.get("/voice/clear", {
          params: {
            dir:_self.dir
          }
        }).then(result => {
          if(result.data==true){
            _self.step=4;
            console.log(result);
            this.$Message.success({
              content: '任务成功结束！',
              duration: 5
            });
            this.modal_step=false;
            this.step=0;
          }else{
            this.$Message.warning("任务执行失败-->缓存清除失败，请稍微再试!")
            this.modal_step=false;
            this.step=0;
          }
        })
          .catch(err => {});
      },
      uploadVoice(){
        let _self = this;
        this.$axios.get("/voice/upload", {
          params: {
            dir:_self.dir
          }
        }).then(result => {
          if(result.data==true){
            _self.step=4;
            console.log(result);
            this.clearVoice();
          }else{
            this.$Message.warning("任务执行失败-->录音文件上传失败，请稍微再试!")
            this.modal_step=false;
            this.step=0;
          }
        })
          .catch(err => {});
      },
      splitVoice(){
        let _self = this;
        this.$axios.get("/voice/split", {
          params: {
            dir:_self.dir
          }
        }).then(result => {
          if(result.data==true){
            _self.step=3;
            console.log(result);
            this.uploadVoice();
          }else{
            this.$Message.warning("任务执行失败-->录音文件切分失败，请稍微再试!")
            this.modal_step=false;
            this.step=0;
          }
        })
          .catch(err => {});
      },
      downVoice(){
        let _self = this;
        this.$axios.get("/voice/download", {
          params: {
            dir:_self.dir
          }
        }).then(result => {
          if(result.data==true){
            _self.step=2;
            console.log(result);
            this.splitVoice();
          }else{
            this.$Message.warning("任务执行失败-->录音文件下载失败，请稍微再试!")
            this.modal_step=false;
            this.step=0;
          }
        })
          .catch(err => {});
      },
      loginShow(){
        this.loginflag=true;
      },
      loginServer(){
        if (("" == this.username) || (""==this.password)) {
          this.$Message.error("用户名和密码不能为空!")
        } else{
          this.login();
        }
      },
      show (row) {
       this.fileId=row.id;
       this.filename=row.name;
       this.filelabel=row.label;
       this.fileurl=row.uri;
       console.log("show:"+this.fileId);
       console.log("show:"+this.fileurl);
       this.modal_play=true;
       this.loadpage();
      },
      update(row){
        if(this.logintime==2) {
          this.fileId = row.id;
          this.filename = row.name;
          this.filelabel = row.label;
          console.log("show:" + this.fileId);
          this.modal_update = true;
        }else{
          this.$Message.warning({
            content: '没有权限，无法修改数据，请与管理员联系！',
            duration: 3
          });
        }
      },
      remove(row) {
        if(this.logintime==2) {
          this.fileId = row.id;
          this.filename = row.name;
          this.filelabel = row.label;
          console.log("show:" + this.fileId);
          this.modal_remove = true;
        }else{
          this.$Message.warning({
            content: '没有权限，无法删除数据，请与管理员联系！',
            duration: 3
          });
        }
      },
      changeDate(date){
        if(this.logintime==1){
          this.page=0;
          this.datetime=date;
          console.log("show:"+this.datetime);
          this.loadpage();
        }else{
          this.$Message.warning("请先登陆!");
        }
      },
      dirclick(e){
        console.log(e);
        this.dir=e;
        this.modal_upload=false;
        this.loadVoice();
        this.modal_voice=true;
      },
      splitclick(e){
        console.log(e);
        this.splitdir=e;
        this.modal_download=false;
        this.loadSplitVoice();
        this.modal_split=true;
      },
      Analysis(){
        this.modal_upload=false;
        this.modal_checkbox=true;
      },
      upload(){
        if(this.logintime==2){
          this.loaddir();
          this.modal_upload=true;
        }else if(this.logintime==0){
          this.$Message.warning("请先登陆");
        }else {
          this.$Message.warning({
            content: '没有权限，无法切分数据，请与管理员联系！',
            duration: 3
          });
        }
      },
      loadData(){
        if(this.logintime==1){
          console.log("flash");
          this.$Message.success("刷新成功");
          this.loadpage();
        }else{
          this.$Message.warning("请先登陆!");
        }
      },
      handlePage(value){
        this.page=value-1;
        console.log((this.page));
        this.loadpage();
      },
      ok_remove(){
        this.delete();
      },
      cancel_remove(){
        this.modal_remove=false;
      },
      ok_update(){
        this.updates();
      },
      cancel_update(){
        this.modal_update=false;
      },
      ok_play(){
        if(this.islisten==false){
          this.$Message.warning({
            content: '试听录音在Check，以防将无效的录音数据判断为有效!',
            duration: 3
          });
        }else{
          this.islisten==false;
          this.check();
          this.modal_play=false;
        }
      },
      uncheck_play(){
        if(this.islisten==false){
          this.$Message.warning({
            content: '试听录音在uncheck，以防将无效的录音数据判断为有效!',
            duration: 3
          });
        }else{
          this.islisten==false;
          this.uncheck();
          this.modal_play=false;
        }
      },
      cancel_play(){
          this.islisten=false;
          this.modal_play=false;
      },
      ok_upload(){
        this.modal_upload=false;
      },
      cancel_upload(){
        this.modal_upload=false;
      },
      ok_split(){
        this.modal_download=false;
      },
      cancel_split(){
        this.modal_download=false;
      },
      ok_voicelist(){
          this.modal_voice=false;
      },
      ok_splitvoicelist(){
        this.modal_split=false;
      },
      ok_modal_checkbox(){
        if(this.analysis_list.length==0){
          this.$Message.warning({
            content: '请先选择需要分析的目录！',
            duration: 3
          });
        }else {
          this.people = [];
          this.patterns = [];
          this.people_0_2_0 = [];
          this.people_0_8_0 = [];
          this.people_1_5_0 = [];
          this.people_2_0_0 = [];
          this.people_4_0_0 = [];
          this.people_0_2_1 = [];
          this.people_0_8_1 = [];
          this.people_1_5_1 = [];
          this.people_2_0_1 = [];
          this.people_4_0_1 = [];
          this.people_0_2_2 = [];
          this.people_0_8_2 = [];
          this.people_1_5_2 = [];
          this.people_2_0_2 = [];
          this.people_4_0_2 = [];
          this.diff_0_2_0 = [];
          this.diff_0_8_0 = [];
          this.diff_1_5_0 = [];
          this.diff_2_0_0 = [];
          this.diff_4_0_0 = [];
          this.diff_0_2_1 = [];
          this.diff_0_8_1 = [];
          this.diff_1_5_1 = [];
          this.diff_2_0_1 = [];
          this.diff_4_0_1 = [];
          this.diff_0_2_2 = [];
          this.diff_0_8_2 = [];
          this.diff_1_5_2 = [];
          this.diff_2_0_2 = [];
          this.diff_4_0_2 = [];
          this.analysis_voicelist = [];
          this.modal_checkbox = false;
          this.modal_analysis = true;
          this.loadVoices(this.analysis_list);
        }
      },
      cancel_modal_checkbox(){
        this.modal_checkbox=false;
      },
      cancel_analysis_voicelist(){
        this.modal_analysis=false;
      },
      play(){
        this.islisten=true;
        this.auObj.play();
      },
      split(){
          this.modal_voice=false;
          this.modal_step=true;
          this.step=1;
          this.downVoice();
          this.$Message.warning({
            content: '任务提交成功,我们需要一点时间执行任务，请耐心等待，不要随意取消任务！',
            duration: 5
        });
      },
      array_difference(a, b) { // 差集 a - b
        var temp=[];
        for(var i=0;i<a.length;i++){
          if(b.indexOf(a[i])<0){
            temp.push(a[i]);
          }
        }
        return temp;
      },
      splitdown(){
        this.modal_split=false;
        for(var i=0;i<this.splitvoicelist.length;i++){
          console.log(this.splitvoicelist[i].uri);
          this.downloadurl.push(this.splitvoicelist[i].uri);
        }
        console.log("地址:"+this.downloadurl)
        this.$Notice.info({
          title: '提醒框',
          desc: '抱歉，我们的下载功能暂时还未开放！ '
        });
        //this.handleBatchDownload();
      },
      handleBatchDownload() {
        const data = this.downloadurl;
        const zip = new JSZip()
        const cache = {}
        const promises = []
        data.forEach(item => {
          const promise = getFile(item).then(data => {
            const arr_name = item.split("/")
            const file_name = arr_name[arr_name.length - 1]
            zip.file(file_name, data, { binary: true })
            cache[file_name] = data
          })
          promises.push(promise)
        })
        Promise.all(promises).then(() => {
          zip.generateAsync({type:"blob"}).then(content => {
            FileSaver.saveAs(content, "打包下载.zip")
          })
        })
      }
    }
  }
</script>
<style scoped>
  .layout{
    border: 1px solid #d7dde4;
    background: #f5f7f9;
    position: relative;
    border-radius: 4px;
    overflow: hidden;
  }
  .layout-logo{
    width: 100px;
    height: 30px;
    background: #5b6270;
    border-radius: 3px;
    float: left;
    position: relative;
    top: 15px;
    left: 20px;
  }
  .layout-nav{
    width: 420px;
    margin: 0 auto;
    margin-right: 20px;
  }
  .layout-footer-center{
    text-align: center;
  }
  .demo-spin-icon-load{
    animation: ani-demo-spin 1s linear infinite;
  }
  @keyframes ani-demo-spin {
    from { transform: rotate(0deg);}
    50%  { transform: rotate(180deg);}
    to   { transform: rotate(360deg);}
  }
  .demo-spin-col{
    height: 100px;
    position: relative;
    border: 1px solid #eee;
  }
  .demo-drawer-footer{
    width: 100%;
    position: absolute;
    bottom: 0;
    left: 0;
    border-top: 1px solid #e8e8e8;
    padding: 10px 16px;
    text-align: right;
    background: #fff;
  }
</style>
