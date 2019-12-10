<template>
    <div id="app">
        <div id="throbber" v-show="isLoading">
            <Throbber :color="throbberColor" :is-loading="isLoading"/>
        </div>
        <div class="header level">
            <div class="level-left">
                <div class="level-item" id="logo-name">
                    <img alt="Logo" id="logo" src="../public/logo.png">
                    <h1 class="title is-1" id="name">Doppelgänger</h1>
                </div>
            </div>
            <div class="level-right" id="input-elements">
                <div class="level-item">
                    <div class="field">
                        <div class="control">
                            <div class="select">
                                <select v-model="projectType">
                                    <option disabled value="">Project Type</option>
                                    <option value="LOCAL">Local Directory</option>
                                    <option value="GIT">Git Repository</option>
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
                            <input
                                    class="input"
                                    placeholder="Base Path"
                                    type="text"
                                    v-model="basePath"
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
            <Graph :graph-data="graphData" @fitted="graphFittedHandler" class="box" id="graph"/>
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
        },
        data() {
            return {
                isLoading: false,
                throbberColor: '#3298DC',
                queryParameters: new URLSearchParams(window.location.search),
                projectType: 'LOCAL',
                projectRoot: '',
                basePath: '',
                cloneType: 'ONE',
                massThreshold: '25',
                cloneClasses: [],
                cloneMetrics: {},
            }
        },
        mounted() {
            this.readParametersFromUrl();
        },
        methods: {
            fetchAnalysis() {
                this.isLoading = true;

                const parameters = {
                    'projectType': this.projectType,
                    'basePath': this.basePath,
                    'projectRoot': this.projectRoot,
                    'cloneType': this.cloneType,
                    'massThreshold': this.massThreshold,
                };

                this.updateUrlQueryParameters();

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
                        this.isLoading = false;
                    });
            },
            graphFittedHandler() {
                this.isLoading = false;
            },
            readParametersFromUrl() {
                this.projectType = this.retrieveQueryParameter('projectType', this.projectType);
                this.projectRoot = this.retrieveQueryParameter('projectRoot', this.projectRoot);
                this.basePath = this.retrieveQueryParameter('basePath', this.basePath);
                this.cloneType = this.retrieveQueryParameter('cloneType', this.cloneType);
                this.massThreshold = this.retrieveQueryParameter('massThreshold', this.massThreshold);
            },
            updateParametersForUrl() {
                this.queryParameters.set('projectType', this.projectType);
                this.queryParameters.set('projectRoot', this.projectRoot);
                this.queryParameters.set('basePath', this.basePath);
                this.queryParameters.set('cloneType', this.cloneType);
                this.queryParameters.set('massThreshold', this.massThreshold);
            },
            retrieveQueryParameter(key, fallback) {
                if (this.queryParameters.has(key)) {
                    return this.queryParameters.get(key);
                } else {
                    return fallback;
                }
            },
            updateUrlQueryParameters() {
                this.updateParametersForUrl();
                const newUrl = decodeURIComponent(`${window.location.origin}${window.location.pathname}?${this.queryParameters.toString()}`);
                history.pushState({}, document.title, newUrl);
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

    #logo-name {
        display: flex;
        flex-direction: row;
        align-items: center;
    }

    #logo {
        max-height: 3em;
        margin-right: 0.5em;
    }

    #name {
        text-shadow: 0 0 6px #3298DC;
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

    #input-elements {
        padding-top: 0.5em;
    }

    #mass-threshold {
        width: 4em;
    }

    #project-root {
        width: 32em;
    }
</style>
