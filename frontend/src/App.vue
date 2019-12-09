<template>
    <div id="app">
        <Graph id="graph" :graph-data="graphData"/>
    </div>
</template>

<script>
    import Graph from './components/Graph.vue'

    import axios from 'axios';


    export default {
        name: 'app',
        components: {
            Graph
        },
        computed: {
            graphData: function () {
                const data = {nodes: [], edges: []};
                for (let cloneClass of this.cloneClasses) {
                    // Clone class nodes
                    const cloneClassUnit = cloneClass[0];
                    const cloneClassUnitId = 'class:' + cloneClassUnit.id;

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
    body {
        margin: 0;
    }

    #graph {
        height: 100vh;
    }
</style>
