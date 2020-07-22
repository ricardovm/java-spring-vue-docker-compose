<template>
  <div>
    <b-modal
      scrollable
      :title="editingItem.name"
      v-model="editDialogVisible"
      ok-title="Save"
      @ok="save(editingItem)"
      :ok-disabled="!canSave"
    >
      <b-form>
        <b-form-group id="input-group-1" label="Name:" label-for="userName">
          <b-form-input
            id="userName"
            v-model="editingItem.name"
            required
            placeholder="Enter user name"
          ></b-form-input>
        </b-form-group>
        <b-form-group id="input-group-2" label="Level:" label-for="userLevel">
          <b-form-select id="userLevel" v-model="editingItem.level" :options="levels" required></b-form-select>
        </b-form-group>
        <b-form-group
          id="input-group-3"
          label="Supervisor:"
          label-for="userSupervisor"
          v-if="editingItem.level == 'DEVELOPER'"
        >
          <b-form-select
            id="userSupervisor"
            v-model="editingItem.supervisor.id"
            :options="supervisors"
            required
            value-field="id"
            text-field="name"
          ></b-form-select>
        </b-form-group>
      </b-form>
      <b-container fluid v-if="editingItem.id">
        <b-row>Developers</b-row>
        <b-table :items="editingItem.developers" :fields="['name']"></b-table>
      </b-container>
      <b-container fluid v-if="editingItem.id">
        <b-row>Projects</b-row>
        <b-table :items="editingItem.projects" :fields="['name', 'actions']">
          <template v-slot:cell(actions)="row">
            <b-button
              variant="danger"
              size="sm"
              @click="confirmDeleteProject(row.item)"
              class="mr-2"
            >Remove</b-button>
          </template>
        </b-table>

        <b-button variant="secondary" @click="addNewProject()" v-if="!addingProject">Add new project</b-button>
        <b-row v-if="addingProject">
          <b-col cols="10">
            <b-form-select
              v-model="selectedProject"
              :options="projects"
              required
              value-field="id"
              text-field="name"
            ></b-form-select>
          </b-col>
          <b-col cols="1">
            <b-button
              size="sm"
              :disabled="!selectedProject"
              @click="addProjectToUser(selectedProject)"
            >Add</b-button>
          </b-col>
        </b-row>
      </b-container>
    </b-modal>

    <h1>Users</h1>
    <b-table :items="items" :fields="fields" striped responsive="sm">
      <template v-slot:cell(supervisor)="row">
        {{
        row.item.supervisor && row.item.supervisor.name
        ? row.item.supervisor.name
        : ""
        }}
      </template>
      <template v-slot:cell(actions)="row">
        <b-button size="sm" @click="edit(row.item)" class="mr-2">Show</b-button>
        <b-button variant="danger" size="sm" @click="confirmDelete(row.item)" class="mr-2">Delete</b-button>
      </template>
    </b-table>
    <b-button variant="primary" @click="showEditDialog({})">Create a new user</b-button>
  </div>
</template>

<script>
import axios from "axios";

const BASE_URL = "http://localhost:9080/users";
const PROJECTS_URL = "http://localhost:9080/projects";

export default {
  data() {
    return {
      fields: ["id", "name", "level", "supervisor", "actions"],
      levels: ["DEVELOPER", "SUPERVISOR"],
      items: [],
      supervisors: [],
      editingItem: { supervisor: {} },
      editDialogVisible: false,
      addingProject: false,
      projects: [],
      selectedProject: null
    };
  },
  methods: {
    loadData() {
      axios.get(`${BASE_URL}`).then(response => (this.items = response.data));
    },
    edit(item) {
      axios
        .get(`${BASE_URL}/${item.id}`)
        .then(response => this.showEditDialog(response.data));
    },
    reloadEditingItem() {
      this.addingProject = false;
      this.selectedProject = null;
      axios
        .get(`${BASE_URL}/${this.editingItem.id}`)
        .then(response => (this.editingItem = response.data));
    },
    showEditDialog(item) {
      this.loadSupervisors();
      this.addingProject = false;
      this.selectedProject = null;

      if (!item.id) {
        item.level = "DEVELOPER";
      }

      if (!item.supervisor) {
        item.supervisor = {};
      }

      this.editingItem = item;
      this.editDialogVisible = true;
    },
    loadSupervisors() {
      axios
        .get(`${BASE_URL}?level=SUPERVISOR`)
        .then(response => (this.supervisors = response.data));
    },
    save(item) {
      if (!item.id) {
        this.create(item);
      } else {
        this.update(item);
      }
    },
    create(item) {
      axios.post(`${BASE_URL}`, item).then(() => this.loadData());
    },
    update(item) {
      axios.put(`${BASE_URL}/${item.id}`, item).then(() => this.loadData());
    },
    confirmDelete(item) {
      this.$bvModal
        .msgBoxConfirm("Do you want to delete this user?", {
          title: item.name,
          size: "sm",
          buttonSize: "sm",
          okVariant: "danger",
          okTitle: "Yes",
          cancelTitle: "No",
          footerClass: "p-2",
          hideHeaderClose: false,
          centered: true
        })
        .then(value => {
          if (value) {
            this.doDelete(item);
          }
        });
    },
    doDelete(item) {
      axios.delete(`${BASE_URL}/${item.id}`).then(() => this.loadData());
    },
    addNewProject() {
      const currentProjects = this.editingItem.projects.map(p => p.id);
      axios
        .get(`${PROJECTS_URL}`)
        .then(
          response =>
            (this.projects = response.data.filter(
              p => !currentProjects.includes(p.id)
            ))
        );
      this.addingProject = true;
    },
    addProjectToUser(projectId) {
      const project = this.projects.filter(p => (p.id = projectId))[0];
      axios
        .post(`${BASE_URL}/${this.editingItem.id}/projects`, project)
        .then(() => this.reloadEditingItem());
    },
    confirmDeleteProject(item) {
      this.$bvModal
        .msgBoxConfirm(
          `Do you want to remove project ${item.name} from this user?`,
          {
            title: this.editingItem.name,
            size: "sm",
            buttonSize: "sm",
            okVariant: "danger",
            okTitle: "Yes",
            cancelTitle: "No",
            footerClass: "p-2",
            hideHeaderClose: false,
            centered: true
          }
        )
        .then(value => {
          if (value) {
            this.doDeleteProject(item);
          }
        });
    },
    doDeleteProject(item) {
      axios
        .delete(`${BASE_URL}/${this.editingItem.id}/projects/${item.id}`)
        .then(() => this.reloadEditingItem());
    }
  },
  computed: {
    canSave() {
      return (
        this.editingItem &&
        this.editingItem.name &&
        this.editingItem.name.trim() !== "" &&
        this.editingItem.level &&
        (this.editingItem.level === "SUPERVISOR" ||
          (this.editingItem.supervisor && this.editingItem.supervisor.id))
      );
    }
  },
  created() {
    this.loadData();
  }
};
</script>
