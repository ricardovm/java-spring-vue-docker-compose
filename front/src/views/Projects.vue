<template>
  <div>
    <b-modal
      scrollable
      :title="editingItem.name"
      v-model="editDialogVisible"
      ok-title="Save"
      @ok="save(editingItem)"
      :ok-disabled="!editingItem.name || editingItem.name.trim() === ''"
    >
      <b-form>
        <b-form-group id="input-group-1" label="Name:" label-for="projectName">
          <b-form-input
            id="projectName"
            v-model="editingItem.name"
            required
            placeholder="Enter project name"
          ></b-form-input>
        </b-form-group>
      </b-form>
      <b-container fluid v-if="editingItem.id">
        <b-row>Leaders</b-row>
        <b-table :items="editingItem.leaders" :fields="['name']"></b-table>
      </b-container>
      <b-container fluid v-if="editingItem.id">
        <b-row>Developers</b-row>
        <b-table :items="editingItem.developers" :fields="['name']"></b-table>
      </b-container>
    </b-modal>

    <h1>Projects</h1>
    <b-table :items="items" :fields="fields" striped responsive="sm">
      <template v-slot:cell(actions)="row">
        <b-button size="sm" @click="edit(row.item)" class="mr-2">Show</b-button>
        <b-button variant="danger" size="sm" @click="confirmDelete(row.item)" class="mr-2">Delete</b-button>
      </template>
    </b-table>
    <b-button variant="primary" @click="showEditDialog({})">Create a new project</b-button>
  </div>
</template>

<script>
import axios from "axios";

const BASE_URL = "http://localhost:9080/projects";

export default {
  data() {
    return {
      fields: ["id", "name", "actions"],
      items: [],
      editingItem: {},
      editDialogVisible: false
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
    showEditDialog(item) {
      this.editingItem = item;
      this.editDialogVisible = true;
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
        .msgBoxConfirm("Do you want to delete this project?", {
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
    }
  },
  created() {
    this.loadData();
  }
};
</script>
