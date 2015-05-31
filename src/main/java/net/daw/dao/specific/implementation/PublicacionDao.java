/*
 * Copyright (C) 2014 al038513
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package net.daw.dao.specific.implementation;

import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import net.daw.bean.specific.implementation.AmistadBean;
import net.daw.bean.specific.implementation.PublicacionBean;
import net.daw.bean.specific.implementation.UsuarioBean;
import net.daw.dao.generic.implementation.TableDaoGenImpl;
import net.daw.helper.statics.AppConfigurationHelper;
import net.daw.helper.statics.ExceptionBooster;
import net.daw.helper.statics.FilterBeanHelper;

/**
 *
 * @author al038513
 */
public class PublicacionDao extends TableDaoGenImpl<PublicacionBean> {

    public PublicacionDao(Connection pooledConnection) throws Exception {
        super(pooledConnection);

    }

    @Override
    public PublicacionBean get(PublicacionBean oPublicacionBean, Integer expand) throws Exception {
        if (oPublicacionBean.getId() > 0) {
            try {
                if (!oMysql.existsOne(strTableOrigin, oPublicacionBean.getId())) {
                    oPublicacionBean.setId(0);
                } else {
                    expand--;
                    if (expand > 0) {
                        oPublicacionBean.setContenido(oMysql.getOne(strTableOrigin, "contenido", oPublicacionBean.getId()));

                        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        String dateInString = oMysql.getOne(strTableOrigin, "fechacreacion", oPublicacionBean.getId());
                        oPublicacionBean.setFechacreacion(formatter.parse(dateInString));

                        oPublicacionBean.setId_usuario(Integer.parseInt(oMysql.getOne(strTableOrigin, "id_usuario", oPublicacionBean.getId())));

                        UsuarioBean oUsuario = new UsuarioBean();
                        oUsuario.setId(Integer.parseInt(oMysql.getOne(strTableOrigin, "id_usuario", oPublicacionBean.getId())));
                        UsuarioDao oUsuarioDAO = new UsuarioDao(oConnection);
                        oUsuario = oUsuarioDAO.get(oUsuario, AppConfigurationHelper.getJsonDepth());
                        oUsuario.setPassword(null);
                        oPublicacionBean.setObj_usuario(oUsuario);
                    }
                }
            } catch (Exception ex) {
                ExceptionBooster.boost(new Exception(this.getClass().getName() + ":get ERROR: " + ex.getMessage()));
            }
        } else {
            oPublicacionBean.setId(0);
        }
        return oPublicacionBean;
    }

    @Override
    public PublicacionBean set(PublicacionBean oPublicacionBean) throws Exception {
        try {
            Boolean isNew = false;

            if (oPublicacionBean.getId() == 0) {
                oPublicacionBean.setId(oMysql.insertOne(strTableOrigin));
                isNew = true;
            }

            oMysql.updateOne(oPublicacionBean.getId(), strTableOrigin, "contenido", oPublicacionBean.getContenido());
            oMysql.updateOne(oPublicacionBean.getId(), strTableOrigin, "id_usuario", oPublicacionBean.getId_usuario().toString());

            if (isNew == false) {
                oMysql.updateOne(oPublicacionBean.getId(), strTableOrigin, "fechacreacion", oMysql.getOne(strTableOrigin, "fechacreacion", oPublicacionBean.getId()));
            } else {
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date newDate = new Date();
                String date = formatter.format(newDate);
                oMysql.updateOne(oPublicacionBean.getId(), strTableOrigin, "fechacreacion", date);
            }
        } catch (Exception ex) {
            ExceptionBooster.boost(new Exception(this.getClass().getName() + ":set ERROR: " + ex.getMessage()));
        }
        return oPublicacionBean;
    }

    public int getPagesComentarioAmigo(int id_usuario, int intRegsPerPag, ArrayList<FilterBeanHelper> hmFilter) throws Exception {
        int pages = 0;
        try {
            pages = oMysql.getPagesComentarioAmigo(id_usuario, intRegsPerPag, hmFilter);
        } catch (Exception ex) {
            ExceptionBooster.boost(new Exception(this.getClass().getName() + ":getPages ERROR: " + ex.getMessage()));
        }
        return pages;
    }

    public ArrayList<PublicacionBean> getPageComentarioAmigo(int id_usuario, int intRegsPerPag, int intPage, ArrayList<FilterBeanHelper> hmFilter, HashMap<String, String> hmOrder) throws Exception {
        ArrayList<Integer> arrId;
        ArrayList<PublicacionBean> arrPublicacion = new ArrayList<>();
        try {
            arrId = oMysql.getPageComentarioAmigo(id_usuario, intRegsPerPag, intPage, hmFilter, hmOrder);
            Iterator<Integer> iterador = arrId.listIterator();
            while (iterador.hasNext()) {
                PublicacionBean oPublicacionBean = new PublicacionBean(iterador.next());
                arrPublicacion.add(this.get(oPublicacionBean, AppConfigurationHelper.getJsonDepth()));
            }
        } catch (Exception ex) {
            ExceptionBooster.boost(new Exception(this.getClass().getName() + ":getPage ERROR: " + ex.getMessage()));
        }
        return arrPublicacion;
    }

    public AmistadBean agregarAmigo(AmistadBean oAmigoBean) throws Exception {
        try {

            oMysql.agregarAmigo(oAmigoBean.getId_usuario_1(), oAmigoBean.getId_usuario_2());

        } catch (Exception ex) {
            ExceptionBooster.boost(new Exception(this.getClass().getName() + ":set ERROR: " + ex.getMessage()));
        }
        return oAmigoBean;
    }
}