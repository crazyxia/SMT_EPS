<template>
  <div class="accessDenied">
    <div class="loading">
    </div>
  </div>
</template>

<script>
  export default {
    name: "AccessDenied"
  }
</script>

<style scoped>
  .accessDenied {
    z-index: 20000;
    position: absolute;
    display: flex;
    width: 100%;
    height:100%;
    justify-content: center;
    align-items: center;
    background:rgba(211,211,211,0.2);
  }

  .loading {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    border-radius: 10px;
    width: 10rem;
    height: 10rem;
  }
</style>
