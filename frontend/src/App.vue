<template>
    <div id="app">
        <div id="throbber" v-show="isLoading">
            <Throbber :color="throbberColor" :is-loading="isLoading"/>
        </div>
        <div class="header level">
            <div class="level-left">
                <div class="level-item">
                    <h1 class="title is-1">Doppelgänger</h1>
                </div>
            </div>
            <div class="level-right input-elements">
                <div class="level-item">
                    <div class="field">
                        <div class="control">
                            <input
                                    class="input"
                                    placeholder="Base Package Identifier"
                                    type="text"
                                    v-model="basePackageIdentifier"
                            >
                        </div>
                    </div>
                </div>
                <div class="level-item">
                    <div class="field">
                        <div class="control">
                            <input
                                    class="input"
                                    id="project-root"
                                    placeholder="Project Root"
                                    type="text"
                                    v-model="projectRoot"
                            >
                        </div>
                    </div>
                </div>
                <div class="level-item">
                    <div class="field">
                        <div class="control">
                            <div class="select">
                                <select v-model="cloneType">
                                    <option disabled value="">Clone Type</option>
                                    <option value="ONE">One</option>
                                    <option value="TWO">Two</option>
                                    <option value="THREE">Three</option>
                                </select>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="level-item">
                    <div class="field">
                        <div class="control">
                            <input
                                    class="input"
                                    id="mass-threshold"
                                    placeholder="Mass Threshold"
                                    step="1"
                                    type="number"
                                    v-model="massThreshold"
                            >
                        </div>
                    </div>
                </div>
                <div class="level-item">
                    <div class="control">
                        <button @click="fetchAnalysis" class="button is-info">Analyze</button>
                    </div>
                </div>
            </div>
        </div>
        <div id="content">
            <Graph :graph-data="graphData" class="box" id="graph"/>
            <Sidebar :metrics="cloneMetrics" class="box" id="sidebar"/>
        </div>
    </div>
</template>

<script>
    import Graph from './components/Graph.vue'
    import Sidebar from './components/Sidebar.vue'
    import Throbber from './components/Throbber.vue'

    import axios from 'axios';


    export default {
        name: 'app',
        components: {
            Graph,
            Sidebar,
            Throbber,
        },
        computed: {
            graphData: function () {
                const data = {nodes: [], edges: []};
                for (let cloneClass of this.cloneClasses) {
                    // Clone class nodes
                    const cloneClassUnit = cloneClass[0];
                    const cloneClassUnitId = cloneClassUnit.hash;

                    const node = {
                        type: 'class',
                        id: cloneClassUnitId,
                        content: cloneClassUnit.content,
                        mass: cloneClassUnit.mass,
                    };
                    data.nodes.push(node);

                    // Unit edges
                    for (let unit of cloneClass) {
                        const unitId = unit.identifier;

                        const node = {
                            type: 'unit',
                            id: unitId,
                            identifier: unit.identifier,
                        };
                        data.nodes.push(node);

                        const edge = {
                            from: cloneClassUnitId,
                            to: unitId,
                            range: unit.range,
                        };
                        data.edges.push(edge);
                    }
                }
                return data;
            },
            basePackageIdentifier: {
                get: function () {
                    return this.retrieveQueryParameter('basePackageIdentifier');
                },
                set: function (newValue) {
                    if (!newValue) return;
                    this.queryParameters.set('basePackageIdentifier', newValue);
                },
            },
            projectRoot: {
                get: function () {
                    return this.retrieveQueryParameter('projectRoot');
                },
                set: function (newValue) {
                    if (!newValue) return;
                    this.queryParameters.set('projectRoot', newValue);
                },
            },
            cloneType: {
                get: function () {
                    return this.retrieveQueryParameter('cloneType');
                },
                set: function (newValue) {
                    if (!newValue) return;
                    this.queryParameters.set('cloneType', newValue);
                },
            },
            massThreshold: {
                get: function () {
                    return this.retrieveQueryParameter('massThreshold');
                },
                set: function (newValue) {
                    if (!newValue) return;
                    this.queryParameters.set('massThreshold', newValue);
                },
            },
        },
        data() {
            return {
                cloneClasses: [],
                cloneMetrics: {},
                queryParameters: new URLSearchParams(window.location.search),
                throbberColor: '#3298dc',
                isLoading: false,
            }
        },
        methods: {
            fetchAnalysis() {
                this.isLoading = true;

                const parameters = {
                    'basePackageIdentifier': this.basePackageIdentifier,
                    'projectRoot': this.projectRoot,
                    'cloneType': this.cloneType,
                    'massThreshold': this.massThreshold,
                };

                axios
                    .get(
                        `http://${this.$backendHost}/analysis`,
                        {
                            params: parameters,
                        },
                    )
                    .then((response) => {
                        this.cloneClasses = response.data["cloneClasses"];
                        this.cloneMetrics = response.data["metrics"];
                        this.isLoading = false;
                    })
                    .catch((error) => {
                        console.error(error.response);
                    });
            },
            retrieveQueryParameter(key) {
                if (this.queryParameters.has(key)) {
                    return this.queryParameters.get(key);
                } else {
                    return null;
                }
            },
        },
    }
</script>

<style>
    @import '~bulma/css/bulma.min.css';

    body {
        font-family: 'Rubik', sans-serif;
        margin: 0;
        padding: 20px;
    }

    #content {
        height: calc(100vh - 120px);
        display: flex;
        flex-direction: row;
    }

    #graph {
        flex-basis: 70%;
        margin-bottom: 0 !important;
        padding: 0 !important;
    }

    #sidebar {
        flex-basis: 28.5%;
        margin-left: 1.5%;
    }

    #throbber {
        position: fixed;
        height: 100vh;
        width: 100vw;
        top: 0;
        left: 0;
        right: 0;
        bottom: 0;
        z-index: 99;
        background-color: rgba(0, 0, 0, 0.25);
        padding: 0;
        margin: 0;
    }

    #mass-threshold {
        width: 4em;
    }

    #project-root {
        width: 32em;
    }
</style>
