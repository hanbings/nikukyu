<template>
  <div>
    <div class="flex flex-row gap-2">
      <input v-for="(item,index) in box"
             :key="index"
             :ref="`ref${index}`"
             v-model="item.value"
             class="input"
             maxlength="1"
             oninput="value"
             type="text"
             @input="onInput(index)"
             @keyup.delete="onDelete(index)"/>
    </div>
  </div>
</template>
<script>
export default {
  data() {
    return {
      box: [{value: ''}, {value: ''}, {value: ''}, {value: ''}, {value: ''}, {value: ''}]
    }
  },
  methods: {
    onInput(index) {
      // index < 5 ，如果是第6格，不触发光标移动至下一个输入框。
      if (this.box[index].value && index < 5) {
        this.$refs['ref' + (index + 1)][0].focus()
      }
      let inputValue = '';
      this.box.forEach(item => {
        inputValue = inputValue + item.value
      })
    },
    onDelete(index) {
      // 如果是第1格，不触发光标移动至上一个输入框
      if (index > 0) {
        this.$refs['ref' + (index - 1)][0].focus()
      }
    }
  }
}
</script>
<style scoped>
.input {
  display: flex;
  width: 36px;
  height: 36px;
  font-size: 14px;
  font-weight: bold;
  color: #212529;
  text-align: center;
  outline: none;
  /* 四边都有细细的黑线 */
  border: 1px solid #9CA3AF;
  border-radius: 6px;

  @apply shadow-sm focus:border-primary-500 focus:border-2;
}
</style>