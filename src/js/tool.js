function undefine(i){
    if("undefined" == typeof i){
      return true
    }else{
      return false
    }
  }
  
  function nullZeroBlank( i ){
    if(i==null)
      return true
    else if(typeof(i)=='string'){
      let str=i.replace("□"," ").trim()
      if(str.length==0)
        return true
    }else if( i==0 )
      return true
  
    return false
  }
  
  function notNullZeroBlank( i ){
    return !nullZeroBlank( i )
  }


function dateFormat(dateString, format) {
  try {
      let date = new Date(dateString) // 根据字符串生成Date对象
      if ("yyyy-MM-dd" === format) {
          let dateFormat = date.getFullYear() + "-";
          dateFormat += date.getMonth() + "-";
          dateFormat += date.getDate();
          return dateFormat;
      } else {
          return "无此格式！"
      }
  } catch (e) {
      return "格式转换错误！"
  }
}
  
  export {
    undefine,
    nullZeroBlank,
    notNullZeroBlank
  }