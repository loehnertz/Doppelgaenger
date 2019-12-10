<template>
    <div id="app">
        <h1 class="title is-1">Doppelgänger</h1>
        <div id="content">
            <Graph :graph-data="graphData" class="box" id="graph"/>
            <Sidebar :metrics="cloneMetrics" class="box" id="sidebar"/>
        </div>
    </div>
</template>

<script>
    import Graph from './components/Graph.vue'
    import Sidebar from './components/Sidebar.vue'

    import axios from 'axios';


    export default {
        name: 'app',
        components: {
            Graph,
            Sidebar,
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
            basePackageIdentifier: function () {
                return this.retrieveQueryParameter('basePackageIdentifier');
            },
            projectRoot: function () {
                return this.retrieveQueryParameter('projectRoot');
            },
            cloneType: function () {
                return this.retrieveQueryParameter('cloneType');
            },
            massThreshold: function () {
                return this.retrieveQueryParameter('massThreshold');
            },
        },
        data() {
            return {
                cloneClasses: [],
                cloneMetrics: {},
                queryParameters: null,
            }
        },
        mounted() {
            this.queryParameters = new URLSearchParams(window.location.search);
            this.fetchAnalysis();
        },
        methods: {
            fetchAnalysis() {
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

    h1 {
        text-align: center;
    }

    #content {
        height: calc(100vh - 100px);
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
</style>
