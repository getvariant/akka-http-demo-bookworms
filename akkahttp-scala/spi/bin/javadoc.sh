#!/bin/bash
#
# Generate Javadoc for this repo.
# Probably not needed.
# 


version=1.0.0
root_dir=$(cd $(dirname $0)/..; pwd)
javadoc_dir=${root_dir}/docs

rm -rf ${javadoc_dir}/*

javadoc -d ${javadoc_dir}  --allow-script-in-comments \
   -sourcepath ${root_dir}/src/main/java \
   -windowtitle "Variant ${version}" \
   -doctitle "Variant Application Iteration Server Standard Extension Library" \
   -header "<a onclick=\"window.top.location.href='http://getvariant.com';\" href=\"#\"> <img style=\"margin-bottom:5px;\" src=\"http://getvariant.com/wp-content/uploads/2016/05/VariantLogoSmall.png\"/> \</a>" \
   -bottom "Release $version. Updated $(date +"%d %b %Y").<br/> Copyright &copy; 2019 <a onclick=\"window.top.location.href='http://getvariant.com';\" href=\"#\">Variant Inc.</a>" \
   -subpackages \
   com.variant.extapi.std
